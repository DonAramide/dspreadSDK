package com.basepos.pos.common.domain.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.basepos.pos.common.domain.enums.TransactionType
import com.basepos.pos.common.utils.Constants
import kotlinx.parcelize.Parcelize

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/3/2024
 */

@Keep
@Parcelize
data class TransactionResponse(
    var aid: String,
    var amount: Double,
    var cashbackAmount: Double = 0.00,
    var appLabel: String,
    var authCode: String? = null,
    var cardExpireDate: String,
    var cardHolderName: String,
    var dateTime: String,
    var maskedPan: String,
    var message: String,
    var rrn: String,
    var stan: String,
    var statusCode: String,
    var terminalID: String,
    var transactionType: TransactionType? = null,
    var currency: String,
): Parcelable {
    fun isApproved(): Boolean = statusCode == Constants.ISO_SUCCESS && !isReversal()
    fun isDeclined(): Boolean = statusCode != Constants.ISO_SUCCESS
    fun isTimeOut(): Boolean = message.lowercase().contains("timeout")
    fun isAutoReversal(): Boolean = transactionType == TransactionType.AUTOREVERSAL
    fun isReversal(): Boolean =
        (transactionType == TransactionType.REVERSAL || transactionType == TransactionType.AUTOREVERSAL) && statusCode == Constants.ISO_SUCCESS

    fun calculateTotalAmount(): Double {
        val cashbackAmount = cashbackAmount
        return amount + cashbackAmount
    }
}
