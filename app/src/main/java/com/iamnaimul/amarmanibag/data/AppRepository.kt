package com.iamnaimul.amarmanibag.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


data class AccountBalance(val account: Account, val balance: Double)

class AppRepository(private val db: AppDatabase) {
    private val accounts = db.accountDao()
    private val txs = db.transactionDao()
    private val settings = db.settingsDao()

    fun observeAccounts() = accounts.observeAll()
    fun observeActiveAccounts() = accounts.observeActive()
    fun observeBalances(): Flow<List<AccountBalance>> =
        accounts.observeBalances().map { rows ->
            rows.map { r ->
                AccountBalance(
                    Account(r.id, r.name, r.type, r.openingBalance, r.iconColor, r.isActive, r.createdAt),
                    r.balance
                )
            }
        }
    fun observeSettings() = settings.observe()

    suspend fun loadTransactionPage(
        query: String,
        type: String,
        accountId: Long?,
        fromDate: String,
        toDate: String,
        limit: Int,
        offset: Int
    ) = txs.loadPage(query, type, accountId, fromDate, toDate, limit, offset)

    fun observeMonthCredit(from: String, to: String) = txs.monthlyCredit(from, to)
    fun observeMonthDebit(from: String, to: String) = txs.monthlyDebit(from, to)
    fun observeMonthCount(from: String, to: String) = txs.monthlyCount(from, to)
    suspend fun getTransaction(id: Long) = txs.get(id)

    // Explicit backup only: this is intentionally not observed by the UI.
    suspend fun allTransactionsForBackup() = txs.getAllForBackup()

    suspend fun saveAccount(a: Account) {
        if (a.id == 0L) accounts.insert(a) else accounts.update(a)
    }
    suspend fun deactivateAccount(id: Long) = accounts.deactivate(id)
    suspend fun activateAccount(id: Long) = accounts.activate(id)

    suspend fun saveTransaction(t: Transaction) {
        if (t.id == 0L) txs.insert(t) else txs.update(t)
    }
    suspend fun deleteTransaction(id: Long) = txs.delete(id)

    suspend fun updateSettings(transform: (Settings) -> Settings) {
        settings.upsert(transform(settings.get() ?: Settings()))
    }

    suspend fun restore(
        restoredAccounts: List<Account>,
        restoredTransactions: List<Transaction>,
        restoredSettings: Settings
    ) {
        db.withTransaction {
            txs.deleteAll()
            accounts.deleteAll()
            settings.deleteAll()
            restoredAccounts.forEach { accounts.insert(it) }
            restoredTransactions.forEach { txs.insert(it) }
            settings.upsert(restoredSettings.copy(id = 1))
        }
    }
}
