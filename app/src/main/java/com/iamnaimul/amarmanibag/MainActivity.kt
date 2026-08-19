package com.iamnaimul.amarmanibag

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.iamnaimul.amarmanibag.ui.App
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var vm: MainViewModel
    private lateinit var backup: BackupManager

    private val chooseFolder = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            backup.setFolderUri(it)
            vm.setBackupUri(it.toString())
            vm.showMessage("ব্যাকআপ ফোল্ডার সংরক্ষণ করা হয়েছে")
        }
    }

    private val restoreFile = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            lifecycleScope.launch {
                backup.restore(it)
                    .onSuccess {
                        vm.showMessage("রিস্টোর সফল")
                    }
                    .onFailure { e ->
                        vm.showMessage(
                            e.message ?: "রিস্টোর ব্যর্থ"
                        )
                    }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as AmarManibagApplication

        backup = BackupManager(
            this,
            app.container.repository
        )

        vm = ViewModelProvider(
            this,
            MainViewModelFactory(app.container.repository)
        )[MainViewModel::class.java]

        setContent {
            val settings by vm.settings.collectAsState()

            AmarManibagTheme(
                mode = settings.themeMode
            ) {
                App(
                    modifier = Modifier.fillMaxSize(),
                    vm = vm,

                    onChooseBackupFolder = {
                        chooseFolder.launch(null)
                    },

                    onCreateBackup = {
                        lifecycleScope.launch {
                            backup.createBackup()
                                .onSuccess {
                                    vm.showMessage("ব্যাকআপ তৈরি হয়েছে")
                                }
                                .onFailure { e ->
                                    vm.showMessage(
                                        e.message ?: "ব্যাকআপ ব্যর্থ"
                                    )
                                }
                        }
                    },

                    onRestore = {
                        restoreFile.launch(
                            arrayOf("application/json")
                        )
                    },

                    onExit = {
                        finishAffinity()
                    }
                )
            }
        }
    }
}