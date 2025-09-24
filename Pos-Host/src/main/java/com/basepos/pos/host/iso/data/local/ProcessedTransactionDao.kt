package com.basepos.pos.host.iso.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.basepos.pos.common.domain.enums.TransactionType
import com.basepos.pos.host.iso.data.local.entities.ProcessedTransactionEntity
import kotlinx.coroutines.flow.Flow

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/3/2024
 */
@Dao
interface ProcessedTransactionDao {
    @Upsert
    suspend fun insertTransaction(processedTransaction: ProcessedTransactionEntity)

    @Query("SELECT * FROM processed_transaction WHERE rrn = :rrn")
    suspend fun fetchTransactionByRrn(rrn: String?): ProcessedTransactionEntity?

    @Query("SELECT * FROM processed_transaction ORDER BY id DESC")
    fun fetchAllTransaction(): Flow<List<ProcessedTransactionEntity>>

    @Query("SELECT * FROM processed_transaction WHERE DATE(createdDate / 1000, 'unixepoch') = DATE('now', 'localtime')")
    suspend fun fetchAllTransactionsForCurrentDay(): List<ProcessedTransactionEntity>?

    @Query(
        """
            SELECT * FROM processed_transaction 
            WHERE DATE(createdDate / 1000, 'unixepoch') = DATE(:selectedDate / 1000, 'unixepoch')
            ORDER BY id DESC
        """
    )
    fun fetchAllTransactionsForDate(selectedDate: Long): Flow<List<ProcessedTransactionEntity>>

    @Query("DELETE FROM processed_transaction WHERE rrn = :rrn")
    suspend fun deleteProcessedTransactionByRrn(rrn: String)

    @Query("DELETE FROM processed_transaction")
    suspend fun deleteAllTransactions()

    @Query("SELECT * FROM processed_transaction WHERE isValid = 1 AND (rrn = :rrn OR stan = :stan) LIMIT 1")
    suspend fun fetchValidTransactionByRrnOrStan(rrn: String?, stan: String?): ProcessedTransactionEntity?

    @Query("DELETE FROM processed_transaction WHERE rrn = :rrn AND transactionType != :transactionType")
    suspend fun deleteProcessedTransactionByRrnAndType(rrn: String, transactionType: TransactionType)
}