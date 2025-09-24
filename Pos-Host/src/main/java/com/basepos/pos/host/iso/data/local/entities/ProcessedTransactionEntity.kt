package com.basepos.pos.host.iso.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.basepos.pos.common.domain.enums.AccountType
import com.basepos.pos.common.domain.enums.TransactionType
import com.basepos.pos.common.domain.models.CardInfo
import com.basepos.pos.common.domain.models.TerminalInfo
import com.basepos.pos.common.domain.models.TransactionResponse
import com.basepos.pos.common.utils.UtilMethods

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/3/2024
 */
@Entity(tableName = "processed_transaction")
data class ProcessedTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val amount: Double,
    val cashbackAmount: Double = 0.00,
    val authCode: String? = null,
    val dateTime: String,
    val message: String,
    val rrn: String,
    val stan: String,
    val statusCode: String,
    val transactionType: TransactionType,
    val forwardingInstitutionId: String? = null,
    val transactionTime: String,
    val transactionDate: String,
    val transactionProcessCode: String,
    val transactionMti: String,
    val createdDate: Long = System.currentTimeMillis(),
    var isValid: Boolean = true,
    var transportEchoData: String? = null,
    var managementData: String? = null,
    var accountType: AccountType? = null,
    var cardInfo: CardInfo? = null,
    var terminalInfo: TerminalInfo? = null,
    val hostDateTime: String? = null,
)

fun ProcessedTransactionEntity.toTransactionResponse(): TransactionResponse {
    return TransactionResponse(
        cardInfo?.aid ?: "N/A",
        amount,
        cashbackAmount,
        cardInfo?.cardLabel ?: "N/A",
        authCode,
        cardInfo?.expiry ?: "N/A",
        cardInfo?.cardHolderName ?: "N/A",
        dateTime,
        UtilMethods.maskCardPAN(cardInfo?.pan ?: ""),
        message,
        rrn,
        stan,
        statusCode,
        terminalInfo?.terminalId ?: "N/A",
        transactionType,
        terminalInfo?.currencyCode ?: "NGN"
    )
}
