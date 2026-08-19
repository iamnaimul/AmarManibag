package com.iamnaimul.amarmanibag.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts ORDER BY isActive DESC, id ASC")
    fun observeAll(): Flow<List<Account>>
    @Query("SELECT * FROM accounts WHERE isActive = 1 ORDER BY id ASC")
    fun observeActive(): Flow<List<Account>>
    @Query("""
        SELECT a.id, a.name, a.type, a.openingBalance, a.iconColor, a.isActive, a.createdAt,
               a.openingBalance +
               COALESCE(SUM(CASE WHEN t.transactionType = 'credit' THEN t.amount ELSE -t.amount END), 0)
               AS balance
        FROM accounts a
        LEFT JOIN transactions t ON t.accountId = a.id
        WHERE a.isActive = 1
        GROUP BY a.id, a.name, a.type, a.openingBalance, a.iconColor, a.isActive, a.createdAt
        ORDER BY a.isActive DESC, a.id ASC
    """)
    fun observeBalances(): Flow<List<AccountBalanceRow>>
    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun get(id: Long): Account?
    @Insert
    suspend fun insert(account: Account): Long
    @Update
    suspend fun update(account: Account)
    @Query("UPDATE accounts SET isActive = 0 WHERE id = :id")
    suspend fun deactivate(id: Long)
    @Query("UPDATE accounts SET isActive = 1 WHERE id = :id")
    suspend fun activate(id: Long)
    @Query("DELETE FROM accounts")
    suspend fun deleteAll()
}

data class AccountBalanceRow(
    val id: Long,
    val name: String,
    val type: String,
    val openingBalance: Double,
    val iconColor: String,
    val isActive: Boolean,
    val createdAt: Long,
    val balance: Double
)

@Dao
interface TransactionDao {
    // Used only for explicit backup/restore, never for normal UI rendering.
    @Query("SELECT * FROM transactions ORDER BY transactionDate DESC, id DESC")
    suspend fun getAllForBackup(): List<Transaction>

    @Query("""
        SELECT * FROM transactions
        WHERE (:query = '' OR description LIKE '%' || :query || '%' OR CAST(amount AS TEXT) LIKE '%' || :query || '%')
          AND (:type = 'all' OR transactionType = :type)
          AND (:accountId IS NULL OR accountId = :accountId)
          AND (:fromDate = '' OR transactionDate >= :fromDate)
          AND (:toDate = '' OR transactionDate <= :toDate)
        ORDER BY transactionDate DESC, id DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun loadPage(
        query: String,
        type: String,
        accountId: Long?,
        fromDate: String,
        toDate: String,
        limit: Int,
        offset: Int
    ): List<Transaction>


    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun get(id: Long): Transaction?
    @Insert
    suspend fun insert(tx: Transaction): Long
    @Update
    suspend fun update(tx: Transaction)
    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun delete(id: Long)
    @Query("SELECT COALESCE(SUM(amount),0) FROM transactions WHERE transactionType='credit' AND transactionDate BETWEEN :from AND :to")
    fun monthlyCredit(from: String, to: String): Flow<Double>
    @Query("SELECT COALESCE(SUM(amount),0) FROM transactions WHERE transactionType='debit' AND transactionDate BETWEEN :from AND :to")
    fun monthlyDebit(from: String, to: String): Flow<Double>
    @Query("SELECT COUNT(*) FROM transactions WHERE transactionDate BETWEEN :from AND :to")
    fun monthlyCount(from: String, to: String): Flow<Int>
    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id=1")
    fun observe(): Flow<Settings?>
    @Query("SELECT * FROM settings WHERE id=1")
    suspend fun get(): Settings?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(settings: Settings)
    @Query("DELETE FROM settings")
    suspend fun deleteAll()
}

@Database(
    entities = [Account::class, Transaction::class, Settings::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun settingsDao(): SettingsDao
}
