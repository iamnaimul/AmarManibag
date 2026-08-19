package com.iamnaimul.amarmanibag.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iamnaimul.amarmanibag.*
import java.time.YearMonth

@Composable
fun ReportScreen(vm: MainViewModel) {
    var month by remember { mutableStateOf(YearMonth.now()) }
    val transactions by vm.reportTransactions.collectAsState()
    val dataVersion by vm.dataVersion.collectAsState()
    val totalCountFlow = remember(month) { vm.monthTransactionCount(month) }
    val totalCount by totalCountFlow.collectAsState(initial = 0)
    val listState = rememberLazyListState()

    LaunchedEffect(month, dataVersion) { vm.refreshReport(month) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
            .distinctUntilChanged()
            .collect { lastIndex ->
                if (lastIndex >= transactions.size - 5) vm.loadMoreReport(month)
            }
    }
    val accounts by vm.accounts.collectAsState()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            Text("মাসিক সামারি", style = MaterialTheme.typography.headlineMedium)
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { month = month.minusMonths(1) }) {
                    Icon(Icons.Default.ArrowBack, "আগের মাস")
                }
                Text("${month.year} / ${month.monthValue}", style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = { month = month.plusMonths(1) }) {
                    Icon(Icons.Default.ArrowForward, "পরের মাস")
                }
            }
        }

        item {
            SectionTitle("এই মাসের লেনদেন (${totalCount}টি)")
        }

        if (transactions.isEmpty()) {
            item { Text("এই মাসে কোনো লেনদেন নেই।") }
        } else {
            items(transactions, key = { it.id }) { t ->
                val a = accounts.firstOrNull { it.id == t.accountId }
                ListItem(
                    modifier = Modifier.fillMaxWidth(),
                    headlineContent = { Text(t.description) },
                    supportingContent = { Text("${t.transactionDate} • ${a?.name ?: "অজানা উৎস"}") },
                    trailingContent = {
                        MoneyText(t.amount, positive = t.transactionType == "credit", negative = t.transactionType == "debit")
                    }
                )
            }
        }
    }
}
