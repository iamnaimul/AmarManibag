package com.iamnaimul.amarmanibag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iamnaimul.amarmanibag.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class MainViewModel(private val repo: AppRepository) : ViewModel() {
    companion object { private const val JOURNAL_PAGE_SIZE = 40 }

    // Small/reference data is safe to keep observed in memory. Transactions are NOT.
    val accounts = repo.observeAccounts().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val activeAccounts = repo.observeActiveAccounts().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val balances = repo.observeBalances().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val settings = repo.observeSettings().map { it ?: Settings() }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Settings())

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()
    fun setQuery(v: String) { _query.value = v }

    private val _typeFilter = MutableStateFlow("all")
    val typeFilter = _typeFilter.asStateFlow()
    fun setTypeFilter(v: String) { _typeFilter.value = v }

    private val _accountFilter = MutableStateFlow<Long?>(null)
    val accountFilter = _accountFilter.asStateFlow()
    fun setAccountFilter(v: Long?) { _accountFilter.value = v }

    private val _dateFrom = MutableStateFlow("")
    private val _dateTo = MutableStateFlow("")
    val dateFrom = _dateFrom.asStateFlow()
    val dateTo = _dateTo.asStateFlow()
    fun setDateFrom(v: String) { _dateFrom.value = v }
    fun setDateTo(v: String) { _dateTo.value = v }

    private val _journalTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val journalTransactions = _journalTransactions.asStateFlow()
    private var journalOffset = 0
    private var journalLoading = false
    private var journalHasMore = true

    private val _reportTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val reportTransactions = _reportTransactions.asStateFlow()
    private var reportOffset = 0
    private var reportLoading = false
    private var reportHasMore = true
    private var reportMonthKey = ""

    private val _dataVersion = MutableStateFlow(0)
    val dataVersion = _dataVersion.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()
    fun clearMessage() { _message.value = null }
    fun showMessage(v: String) { _message.value = v }

    val month = YearMonth.now()
    val monthCredit = repo.observeMonthCredit(month.atDay(1).toString(), month.atEndOfMonth().toString())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)
    val monthDebit = repo.observeMonthDebit(month.atDay(1).toString(), month.atEndOfMonth().toString())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    init {
        viewModelScope.launch {
            if (repo.observeSettings().first() == null) repo.updateSettings { Settings() }
        }
    }

    fun refreshJournal() {
        viewModelScope.launch {
            journalLoading = true
            journalOffset = 0
            journalHasMore = true
            val page = repo.loadTransactionPage(
                _query.value.trim(), _typeFilter.value, _accountFilter.value,
                _dateFrom.value.trim(), _dateTo.value.trim(), JOURNAL_PAGE_SIZE, 0
            )
            _journalTransactions.value = page
            journalOffset = page.size
            journalHasMore = page.size == JOURNAL_PAGE_SIZE
            journalLoading = false
        }
    }

    fun loadMoreJournal() {
        if (journalLoading || !journalHasMore) return
        viewModelScope.launch {
            journalLoading = true
            val page = repo.loadTransactionPage(
                _query.value.trim(), _typeFilter.value, _accountFilter.value,
                _dateFrom.value.trim(), _dateTo.value.trim(), JOURNAL_PAGE_SIZE, journalOffset
            )
            if (page.isEmpty()) journalHasMore = false
            else {
                _journalTransactions.value = _journalTransactions.value + page
                journalOffset += page.size
                journalHasMore = page.size == JOURNAL_PAGE_SIZE
            }
            journalLoading = false
        }
    }

    fun monthTransactionCount(month: YearMonth): Flow<Int> =
        repo.observeMonthCount(month.atDay(1).toString(), month.atEndOfMonth().toString())

    fun refreshReport(month: YearMonth) {
        viewModelScope.launch {
            reportLoading = true
            reportMonthKey = month.toString()
            reportOffset = 0
            reportHasMore = true
            val page = repo.loadTransactionPage(
                "", "all", null, month.atDay(1).toString(), month.atEndOfMonth().toString(), JOURNAL_PAGE_SIZE, 0
            )
            _reportTransactions.value = page
            reportOffset = page.size
            reportHasMore = page.size == JOURNAL_PAGE_SIZE
            reportLoading = false
        }
    }

    fun loadMoreReport(month: YearMonth) {
        if (reportLoading || !reportHasMore || reportMonthKey != month.toString()) return
        viewModelScope.launch {
            reportLoading = true
            val page = repo.loadTransactionPage(
                "", "all", null, month.atDay(1).toString(), month.atEndOfMonth().toString(), JOURNAL_PAGE_SIZE, reportOffset
            )
            if (page.isEmpty()) reportHasMore = false
            else {
                _reportTransactions.value = _reportTransactions.value + page
                reportOffset += page.size
                reportHasMore = page.size == JOURNAL_PAGE_SIZE
            }
            reportLoading = false
        }
    }

    fun saveAccount(id: Long?, name: String, type: String, opening: String, color: String) {
        val amount = opening.replace(",", "").toDoubleOrNull()
        if (name.isBlank()) { _message.value = "টাকার উৎসের নাম লিখুন"; return }
        if (amount == null || amount < 0) { _message.value = "শুরুর ব্যালেন্স সঠিক নয়"; return }
        viewModelScope.launch {
            try {
                repo.saveAccount(Account(id ?: 0L, name.trim(), type, amount, color, true))
                _message.value = "টাকার উৎস সংরক্ষণ হয়েছে"
            } catch (e: Exception) { _message.value = "সংরক্ষণ ব্যর্থ: ${e.message}" }
        }
    }

    fun deactivateAccount(id: Long) {
        viewModelScope.launch {
            try { repo.deactivateAccount(id); _message.value = "টাকার উৎস নিষ্ক্রিয় করা হয়েছে" }
            catch (e: Exception) { _message.value = "নিষ্ক্রিয় করা যায়নি: ${e.message}" }
        }
    }

    fun activateAccount(id: Long) {
        viewModelScope.launch {
            try { repo.activateAccount(id); _message.value = "টাকার উৎস সক্রিয় করা হয়েছে" }
            catch (e: Exception) { _message.value = "সক্রিয় করা যায়নি: ${e.message}" }
        }
    }

    fun saveTransaction(id: Long?, accountId: Long?, type: String, description: String, amountText: String, date: String) {
        val amount = amountText.replace(",", "").toDoubleOrNull()
        val validDate = runCatching { LocalDate.parse(date) }.isSuccess
        when {
            accountId == null -> { _message.value = "টাকার উৎস নির্বাচন করুন"; return }
            description.isBlank() -> { _message.value = "লেনদেনের বিবরণ লিখুন"; return }
            amount == null || amount <= 0 -> { _message.value = "পরিমাণ ০-এর বেশি হতে হবে"; return }
            !validDate -> { _message.value = "তারিখ YYYY-MM-DD হতে হবে"; return }
        }
        viewModelScope.launch {
            try {
                val old = id?.let { repo.getTransaction(it) }
                repo.saveTransaction(Transaction(
                    id = id ?: 0L, accountId = accountId, transactionDate = date,
                    transactionType = type, description = description.trim(), amount = amount,
                    createdAt = old?.createdAt ?: System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                ))
                refreshJournal()
                _dataVersion.update { it + 1 }
                _message.value = if (id == null) "লেনদেন সংরক্ষণ হয়েছে" else "লেনদেন আপডেট হয়েছে"
            } catch (e: Exception) { _message.value = "লেনদেন সংরক্ষণ ব্যর্থ: ${e.message}" }
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            try { repo.deleteTransaction(id); refreshJournal(); _dataVersion.update { it + 1 }; _message.value = "লেনদেন মুছে ফেলা হয়েছে" }
            catch (e: Exception) { _message.value = "মুছতে সমস্যা: ${e.message}" }
        }
    }

    fun setTheme(mode: String) { viewModelScope.launch { repo.updateSettings { it.copy(themeMode = mode) } } }
    fun setBackupUri(uri: String?) { viewModelScope.launch { repo.updateSettings { it.copy(backupTreeUri = uri) } } }


}

class MainViewModelFactory(private val repository: AppRepository) :
    androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = MainViewModel(repository) as T
}
