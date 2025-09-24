package com.basepos.pos.common.domain.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.basepos.pos.common.domain.enums.AccountType
import com.basepos.pos.common.domain.enums.TransactionType
import kotlinx.parcelize.Parcelize

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/3/2024
 */

@Keep
@Parcelize
data class PaymentRequest(
    val transType: TransactionType,
    var amount: Double,
    var cashbackAmount: Double = 0.00,
    var accountType: AccountType = AccountType.DEFAULT,
    val print: Boolean? = false,
    var stan: String? = null,
    var rrn: String? = null,
): Parcelable