package com.basepos.pos.common.domain.enums

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/3/2024
 */
enum class CurrencyCode(val isoCode: String, val code: String, val symbol: String) {
    NIGERIA("0566", "566", "NGN"),
    GHANA("0936", "936", "GHS"),
    KENYA("0404", "404", "KES");

    companion object {
        fun fromCode(code: String): CurrencyCode {
            val currencyCode = entries.find { it.code == code }
            return currencyCode ?: NIGERIA
        }
    }
}