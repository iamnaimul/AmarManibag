package com.iamnaimul.amarmanibag.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iamnaimul.amarmanibag.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun App(
    modifier: Modifier,
    vm: MainViewModel,
    onChooseBackupFolder: () -> Unit,
    onCreateBackup: () -> Unit,
    onRestore: () -> Unit,
    onExit: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 4 })
    var exitDialog by remember { mutableStateOf(false) }
    val message by vm.message.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearMessage()
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                val items = listOf(
                    Triple(Icons.Default.Home, "হোম", 0),
                    Triple(Icons.Default.List, "জার্নাল", 1),
                    Triple(Icons.Default.Assessment, "সামারি", 2),
                    Triple(Icons.Default.Settings, "সেটিংস", 3)
                )
                items.forEach { (icon, label, page) ->
                    NavigationBarItem(
                        selected = pagerState.currentPage == page,
                        onClick = { scope.launch { pagerState.animateScrollToPage(page) } },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                beyondViewportPageCount = 0
            ) { page ->
                when (page) {
                    0 -> DashboardScreen(vm)
                    1 -> TransactionsScreen(vm, onBack = {})
                    2 -> ReportScreen(vm)
                    3 -> SettingsScreen(
                        vm = vm,
                        onChooseFolder = onChooseBackupFolder,
                        onBackup = onCreateBackup,
                        onRestore = onRestore,
                        onExit = { exitDialog = true }
                    )
                }
            }

        }
    }

    BackHandler {
        if (pagerState.currentPage != 0) {
            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
        } else {
            exitDialog = true
        }
    }

    if (exitDialog) {
        ConfirmDialog(
            "অ্যাপ থেকে বের হন?",
            "আপনি কি অ্যাপ থেকে বের হতে চান?",
            onConfirm = onExit,
            onDismiss = { exitDialog = false }
        )
    }
}

@Composable
fun DashboardScreen(vm: MainViewModel) {
    val balances by vm.balances.collectAsState()
    val activeAccounts by vm.activeAccounts.collectAsState()
    val income by vm.monthCredit.collectAsState()
    val expense by vm.monthDebit.collectAsState()
    val total = balances.sumOf { it.balance }
    var showForm by remember { mutableStateOf(false) }

    if (showForm) {
        TransactionDialog(
            accounts = activeAccounts,
            existing = null,
            onDismiss = { showForm = false },
            onSave = { id, accountId, type, description, amount, date ->
                vm.saveTransaction(id, accountId, type, description, amount, date)
                showForm = false
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showForm = true }) {
                Icon(Icons.Default.Add, contentDescription = "নতুন লেনদেন")
            }
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(pad),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text("আমার মানিব্যাগ", style = MaterialTheme.typography.headlineMedium)
                Text("ব্যক্তিগত হিসাব • সম্পূর্ণ অফলাইন", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(18.dp)) {
                        Text("সর্বমোট ব্যালেন্স", style = MaterialTheme.typography.titleMedium)
                        Text(money(total), style = MaterialTheme.typography.headlineLarge)
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column { Text("এই মাসে জমা"); MoneyText(income, positive = true) }
                            Column { Text("এই মাসে খরচ"); MoneyText(expense, negative = true) }
                        }
                    }
                }
            }

            item { SectionTitle("টাকার উৎস") }
            if (balances.isEmpty()) {
                item { Text("সেটিংস থেকে প্রথম টাকার উৎস যোগ করুন।") }
            } else {
                // LazyColumn renders only the visible account cards, while balances are already SQL-aggregated.
                items(balances, key = { it.account.id }) { b ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(
                            Modifier.padding(14.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(b.account.name, style = MaterialTheme.typography.titleMedium)
                                Text(accountTypeBn(b.account.type))
                            }
                            MoneyText(b.balance)
                        }
                    }
                }
            }
        }
    }
}

fun accountTypeBn(type: String) = when(type) {
    "bank" -> "ব্যাংক হিসাব"
    "mfs" -> "মোবাইল ফাইন্যান্সিয়াল সার্ভিস"
    "cash" -> "ক্যাশ"
    else -> "অন্যান্য"
}
