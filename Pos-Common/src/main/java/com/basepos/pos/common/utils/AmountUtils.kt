package com.basepos.pos.common.utils

import org.jpos.iso.ISOCurrency
import java.text.DecimalFormat

/**
 * @Author: ifechukwu.udorji
 * @Date: 6/3/2024
 */
object AmountUtils {

    fun toIsoAmount(amount: Double, currencyCode: String): String {
        if (currencyCode.isEmpty())
            return ""

        // Handle zero amounts properly - return proper ISO format instead of empty string
        if (amount == 0.0) {
            return "000000000000"
        }

        return ISOCurrency.convertToIsoMsg(amount, currencyCode)
    }

    fun formatToTwoDecimalPlaces(amount: String): String {
        val decimalFormat = DecimalFormat("#,##0.00")
        val doubleAmount = amount.toDoubleOrNull()
        doubleAmount?.let {
            return decimalFormat.format(it)
        }

        return amount
    }

    fun formatToTwoDecimalPlaces(amount: Double): String {
        val decimalFormat = DecimalFormat("#,##0.00")
        return decimalFormat.format(amount)
    }

    fun cleanAmount(amount: String?): String? {
        return amount?.replace(",", "")
    }

    fun addCashbackToAmount(amount: Double, cashbackAmount: Double): Double {
        return amount + cashbackAmount
    }
}