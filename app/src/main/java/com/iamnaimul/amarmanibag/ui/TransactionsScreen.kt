package com.iamnaimul.amarmanibag.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.delay
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iamnaimul.amarmanibag.*
import com.iamnaimul.amarmanibag.data.Account
import com.iamnaimul.amarmanibag.data.Transaction
import java.time.LocalDate

@Composable
fun TransactionsScreen(
    vm: MainViewModel,
    onBack: () -> Unit
) {
    val txs by vm.journalTransactions.collectAsState()
    val accounts by vm.accounts.collectAsState()

    val query by vm.query.collectAsState()
    val typeFilter by vm.typeFilter.collectAsState()
    val accountFilter by vm.accountFilter.collectAsState()
    val dateFrom by vm.dateFrom.collectAsState()
    val dateTo by vm.dateTo.collectAsState()

    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Transaction?>(null) }
    var deleteId by remember { mutableStateOf<Long?>(null) }
    var detail by remember { mutableStateOf<Transaction?>(null) }
    var filterExpanded by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(query, typeFilter, accountFilter, dateFrom, dateTo) {
        delay(200)
        vm.refreshJournal()
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
            .distinctUntilChanged()
            .collect { lastIndex ->
                if (lastIndex >= txs.size - 5) vm.loadMoreJournal()
            }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editing = null
                    showForm = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "নতুন লেনদেন"
                )
            }
        }
    ) { pad ->

        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp)
        ) {

            Text(
                text = "জার্নাল",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = query,
                onValueChange = vm::setQuery,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("বিবরণ দিয়ে সার্চ")
                },
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                FilterChip(
                    selected = typeFilter == "all",
                    onClick = {
                        vm.setTypeFilter("all")
                    },
                    label = {
                        Text("সব")
                    }
                )

                FilterChip(
                    selected = typeFilter == "credit",
                    onClick = {
                        vm.setTypeFilter("credit")
                    },
                    label = {
                        Text("জমা")
                    }
                )

                FilterChip(
                    selected = typeFilter == "debit",
                    onClick = {
                        vm.setTypeFilter("debit")
                    },
                    label = {
                        Text("খরচ")
                    }
                )
            }

            Spacer(Modifier.height(8.dp))

            Box {

                OutlinedButton(
                    onClick = {
                        filterExpanded = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        accounts
                            .firstOrNull { it.id == accountFilter }
                            ?.name
                            ?: "সব টাকার উৎস"
                    )
                }

                DropdownMenu(
                    expanded = filterExpanded,
                    onDismissRequest = {
                        filterExpanded = false
                    }
                ) {

                    DropdownMenuItem(
                        text = {
                            Text("সব টাকার উৎস")
                        },
                        onClick = {
                            vm.setAccountFilter(null)
                            filterExpanded = false
                        }
                    )

                    accounts.forEach { account ->

                        DropdownMenuItem(
                            text = {
                                Text(account.name)
                            },
                            onClick = {
                                vm.setAccountFilter(account.id)
                                filterExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OutlinedTextField(
                    value = dateFrom,
                    onValueChange = vm::setDateFrom,
                    modifier = Modifier.weight(1f),
                    label = {
                        Text("শুরু (YYYY-MM-DD)")
                    },
                    singleLine = true
                )

                OutlinedTextField(
                    value = dateTo,
                    onValueChange = vm::setDateTo,
                    modifier = Modifier.weight(1f),
                    label = {
                        Text("শেষ (YYYY-MM-DD)")
                    },
                    singleLine = true
                )
            }

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {

                items(
                    items = txs,
                    key = { it.id }
                ) { transaction ->

                    val account = accounts.firstOrNull {
                        it.id == transaction.accountId
                    }

                    Card(
                        onClick = {
                            detail = transaction
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {

                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = transaction.description,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = "${transaction.transactionDate} • " +
                                            "${account?.name ?: "অজানা উৎস"}"
                                )

                                MoneyText(
                                    transaction.amount,
                                    positive = transaction.transactionType == "credit",
                                    negative = transaction.transactionType == "debit"
                                )
                            }

                            Row {

                                IconButton(
                                    onClick = {
                                        editing = transaction
                                        showForm = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "সম্পাদনা"
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        deleteId = transaction.id
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "মুছুন"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // নতুন / সম্পাদনা লেনদেন
    if (showForm) {

        TransactionDialog(
            accounts = accounts,
            existing = editing,

            onDismiss = {
                showForm = false
            },

            onSave = { id, accountId, type, description, amount, date ->

                vm.saveTransaction(
                    id,
                    accountId,
                    type,
                    description,
                    amount,
                    date
                )

                showForm = false
            }
        )
    }

    // লেনদেনের বিস্তারিত
    detail?.let { transaction ->

        val account = accounts.firstOrNull {
            it.id == transaction.accountId
        }

        AlertDialog(
            onDismissRequest = {
                detail = null
            },

            title = {
                Text("লেনদেনের বিস্তারিত")
            },

            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    Text(
                        "বিবরণ: ${transaction.description}"
                    )

                    Text(
                        "তারিখ: ${transaction.transactionDate}"
                    )

                    Text(
                        "টাকার উৎস: ${account?.name ?: "অজানা"}"
                    )

                    Text(
                        "ধরন: ${
                            if (transaction.transactionType == "credit")
                                "জমা / Credit"
                            else
                                "খরচ / Debit"
                        }"
                    )

                    Text(
                        "পরিমাণ: ${money(transaction.amount)}"
                    )
                }
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        detail = null
                    }
                ) {
                    Text("বন্ধ")
                }
            }
        )
    }

    // লেনদেন মুছে ফেলার confirmation
    deleteId?.let { id ->

        ConfirmDialog(
            "লেনদেন মুছবেন?",
            "আপনি কি নিশ্চিতভাবে এই লেনদেনটি মুছে ফেলতে চান?",

            onConfirm = {
                vm.deleteTransaction(id)
                deleteId = null
            },

            onDismiss = {
                deleteId = null
            }
        )
    }
}


@Composable
fun TransactionDialog(
    accounts: List<Account>,
    existing: Transaction?,
    onDismiss: () -> Unit,
    onSave: (
        Long?,
        Long?,
        String,
        String,
        String,
        String
    ) -> Unit
) {

    var accountId by remember {
        mutableStateOf(
            existing?.accountId
                ?: accounts.firstOrNull()?.id
        )
    }

    var type by remember {
        mutableStateOf(
            existing?.transactionType ?: "debit"
        )
    }

    var description by remember {
        mutableStateOf(
            existing?.description ?: ""
        )
    }

    var amount by remember {
        mutableStateOf(
            existing?.amount?.toString() ?: ""
        )
    }

    var date by remember {
        mutableStateOf(
            existing?.transactionDate
                ?: LocalDate.now().toString()
        )
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                if (existing == null)
                    "নতুন লেনদেন"
                else
                    "লেনদেন সম্পাদনা"
            )
        },

        text = {

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // টাকার উৎস নির্বাচন
                Box {

                    OutlinedButton(
                        onClick = {
                            expanded = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            accounts
                                .firstOrNull {
                                    it.id == accountId
                                }
                                ?.name
                                ?: "টাকার উৎস নির্বাচন করুন"
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {

                        accounts
                            .filter { it.isActive }
                            .forEach { account ->

                                DropdownMenuItem(
                                    text = {
                                        Text(account.name)
                                    },

                                    onClick = {
                                        accountId = account.id
                                        expanded = false
                                    }
                                )
                            }
                    }
                }

                // লেনদেনের ধরন
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    FilterChip(
                        selected = type == "credit",
                        onClick = {
                            type = "credit"
                        },
                        label = {
                            Text("জমা")
                        }
                    )

                    FilterChip(
                        selected = type == "debit",
                        onClick = {
                            type = "debit"
                        },
                        label = {
                            Text("খরচ")
                        }
                    )
                }

                // বিবরণ
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    label = {
                        Text("বিবরণ")
                    },
                    singleLine = true
                )

                // পরিমাণ
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                    },
                    label = {
                        Text("পরিমাণ")
                    },
                    singleLine = true
                )

                // তারিখ
                OutlinedTextField(
                    value = date,
                    onValueChange = {
                        date = it
                    },
                    label = {
                        Text("তারিখ (YYYY-MM-DD)")
                    },
                    singleLine = true
                )
            }
        },

        confirmButton = {

            Button(
                onClick = {
                    onSave(
                        existing?.id,
                        accountId,
                        type,
                        description,
                        amount,
                        date
                    )
                }
            ) {
                Text("সংরক্ষণ")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("বাতিল")
            }
        }
    )
}