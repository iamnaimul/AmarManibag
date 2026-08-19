package com.iamnaimul.amarmanibag.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iamnaimul.amarmanibag.*
import com.iamnaimul.amarmanibag.data.Account

@Composable
fun SettingsScreen(
    vm: MainViewModel,
    onChooseFolder: () -> Unit,
    onBackup: () -> Unit,
    onRestore: () -> Unit,
    onExit: () -> Unit
) {
    val accounts by vm.accounts.collectAsState()
    val settings by vm.settings.collectAsState()

    var showAccount by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Account?>(null) }
    var deactivate by remember { mutableStateOf<Account?>(null) }

    // Settings null হলে system theme ব্যবহার হবে
    val themeMode = settings.themeMode

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                "সেটিংস",
                style = MaterialTheme.typography.headlineMedium
            )

            SectionTitle("টাকার উৎস")

            Button(
                onClick = {
                    editing = null
                    showAccount = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("টাকার উৎস যোগ করুন")
            }

            Spacer(Modifier.height(8.dp))
        }

        items(
            accounts,
            key = { it.id }
        ) { a ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        a.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(accountTypeBn(a.type))

                    Text(
                        "শুরুর ব্যালেন্স: ${money(a.openingBalance)}"
                    )

                    Row {
                        TextButton(
                            onClick = {
                                editing = a
                                showAccount = true
                            }
                        ) {
                            Text("সম্পাদনা")
                        }

                        if (a.isActive) {
                            TextButton(
                                onClick = {
                                    deactivate = a
                                }
                            ) {
                                Text("নিষ্ক্রিয়")
                            }
                        } else {
                            TextButton(
                                onClick = {
                                    vm.activateAccount(a.id)
                                }
                            ) {
                                Text("সক্রিয়")
                            }
                        }
                    }
                }
            }
        }

        item {
            SectionTitle("থিম")

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = themeMode == "system",
                    onClick = {
                        vm.setTheme("system")
                    },
                    label = {
                        Text("সিস্টেম")
                    }
                )

                FilterChip(
                    selected = themeMode == "light",
                    onClick = {
                        vm.setTheme("light")
                    },
                    label = {
                        Text("লাইট")
                    }
                )

                FilterChip(
                    selected = themeMode == "dark",
                    onClick = {
                        vm.setTheme("dark")
                    },
                    label = {
                        Text("ডার্ক")
                    }
                )
            }

            SectionTitle("ব্যাকআপ ও রিস্টোর")

            Text(
                "ব্যাকআপ ফোল্ডার নির্বাচন করলে তার ভেতরে " +
                        "AmarMoneyBag/BackUps কাঠামো ব্যবহার করা হবে।"
            )

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = onChooseFolder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("ব্যাকআপ ফোল্ডার নির্বাচন")
            }

            Button(
                onClick = onBackup,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("JSON ব্যাকআপ তৈরি করুন")
            }

            OutlinedButton(
                onClick = onRestore,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("JSON থেকে রিস্টোর করুন")
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onExit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("বের হন")
            }

            Spacer(Modifier.height(24.dp))

            Text("Amar Money Bag • v1.0.190826")
            Text("Developed by Naimul Hassan [A gift to RabbatuALBayt]")
        }
    }

    if (showAccount) {
        AccountDialog(
            existing = editing,
            onDismiss = {
                showAccount = false
            },
            onSave = { id, name, type, opening, color ->
                vm.saveAccount(
                    id,
                    name,
                    type,
                    opening,
                    color
                )
                showAccount = false
            }
        )
    }

    deactivate?.let { a ->
        ConfirmDialog(
            "টাকার উৎস নিষ্ক্রিয় করবেন?",
            "ঐতিহাসিক লেনদেন থাকবে, শুধু উৎসটি নতুন লেনদেনে ব্যবহারযোগ্য থাকবে না।",
            onConfirm = {
                vm.deactivateAccount(a.id)
                deactivate = null
            },
            onDismiss = {
                deactivate = null
            }
        )
    }
}

@Composable
private fun AccountDialog(
    existing: Account?,
    onDismiss: () -> Unit,
    onSave: (Long?, String, String, String, String) -> Unit
) {
    var name by remember {
        mutableStateOf(existing?.name ?: "")
    }

    var type by remember {
        mutableStateOf(existing?.type ?: "cash")
    }

    var opening by remember {
        mutableStateOf(
            existing?.openingBalance?.toString() ?: ""
        )
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    val types = listOf(
        "bank" to "ব্যাংক হিসাব",
        "mfs" to "মোবাইল ফাইন্যান্সিয়াল সার্ভিস",
        "cash" to "ক্যাশ",
        "other" to "অন্যান্য"
    )

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                if (existing == null)
                    "টাকার উৎস যোগ করুন"
                else
                    "টাকার উৎস সম্পাদনা"
            )
        },

        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    label = {
                        Text("নাম")
                    },
                    singleLine = true
                )

                Box {
                    OutlinedButton(
                        onClick = {
                            expanded = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            types.first {
                                it.first == type
                            }.second
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {
                        types.forEach { (value, label) ->

                            DropdownMenuItem(
                                text = {
                                    Text(label)
                                },
                                onClick = {
                                    type = value
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = opening,
                    onValueChange = {
                        opening = it
                    },
                    label = {
                        Text("শুরুর ব্যালেন্স")
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
                        name,
                        type,
                        opening,
                        "#008577"
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