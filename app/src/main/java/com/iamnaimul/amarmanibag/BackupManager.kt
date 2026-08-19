package com.iamnaimul.amarmanibag

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.iamnaimul.amarmanibag.data.*
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BackupPayload(
    val schemaVersion: Int = 1,
    val app: String = "Amar Manibag",
    val exportedAt: String,
    val accounts: List<Account>,
    val transactions: List<Transaction>,
    val settings: Settings
)

class BackupManager(private val context: Context, private val repository: AppRepository) {
    private val gson = GsonBuilder().setPrettyPrinting().create()

    fun folderUri(): Uri? =
        context.getSharedPreferences("backup", Context.MODE_PRIVATE)
            .getString("treeUri", null)?.let(Uri::parse)

    fun setFolderUri(uri: Uri) {
        runCatching {
            context.contentResolver.takePersistableUriPermission(
                uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        }
        context.getSharedPreferences("backup", Context.MODE_PRIVATE)
            .edit().putString("treeUri", uri.toString()).apply()
    }

    suspend fun createBackup(): Result<Uri> = runCatching {
        val tree = folderUri() ?: error("ব্যাকআপ ফোল্ডার নির্বাচন করুন।")
        val selected = DocumentFile.fromTreeUri(context, tree) ?: error("ব্যাকআপ ফোল্ডার পাওয়া যায়নি।")
        val root = if (selected.name == "AmarMoneyBag") selected
        else selected.findFile("AmarMoneyBag") ?: selected.createDirectory("AmarMoneyBag")
        ?: error("AmarMoneyBag ফোল্ডার তৈরি করা যায়নি।")
        val backups = root.findFile("BackUps") ?: root.createDirectory("BackUps")
        ?: error("backups ফোল্ডার তৈরি করা যায়নি।")

        val accounts = repository.observeAccounts().first()
        val transactions = repository.allTransactionsForBackup()
        val settings = repository.observeSettings().first() ?: Settings()
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val name = "amar_manibag_backup_$stamp.json"
        val file = backups.createFile("application/json", name) ?: error("ব্যাকআপ ফাইল তৈরি করা যায়নি।")
        val payload = BackupPayload(
            exportedAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(Date()),
            accounts = accounts,
            transactions = transactions,
            settings = settings
        )
        val jsonOut = gson.toJson(payload)
        context.contentResolver.openOutputStream(file.uri)?.use {
            it.writer(Charsets.UTF_8).use { writer -> writer.write(jsonOut) }
        } ?: error("ফাইলে লেখা যায়নি।")

        root.findFile("amar_manibag_latest_backup.json")?.delete()
        root.createFile("application/json", "amar_manibag_latest_backup.json")?.let { latest ->
            context.contentResolver.openOutputStream(latest.uri)?.use {
                it.writer(Charsets.UTF_8).use { writer -> writer.write(jsonOut) }
            }
        }
        file.uri
    }

    suspend fun restore(uri: Uri): Result<Unit> = runCatching {
        val json = context.contentResolver.openInputStream(uri)?.use {
            it.reader(Charsets.UTF_8).readText()
        } ?: error("ফাইল পড়া যায়নি।")
        val root = JsonParser.parseString(json).asJsonObject
        require(root.get("schemaVersion")?.asInt == 1) { "অসমর্থিত ব্যাকআপ ভার্সন।" }
        require(root.has("accounts") && root.has("transactions") && root.has("settings")) {
            "ব্যাকআপ ফাইল অসম্পূর্ণ।"
        }
        val payload = gson.fromJson(root, BackupPayload::class.java)
        require(payload.accounts.all { it.name.isNotBlank() && it.id > 0 }) { "টাকার উৎসের ডেটা অবৈধ।" }
        require(payload.transactions.all { it.id > 0 && it.accountId > 0 && it.amount > 0.0 &&
            (it.transactionType == "credit" || it.transactionType == "debit") }) {
            "লেনদেনের ডেটা অবৈধ।"
        }
        val accountIds = payload.accounts.map { it.id }.toSet()
        require(payload.transactions.all { it.accountId in accountIds }) {
            "কিছু লেনদেনের টাকার উৎস পাওয়া যায়নি।"
        }
        repository.restore(payload.accounts, payload.transactions, payload.settings)
    }
}
