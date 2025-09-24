package com.basepos.pos.host.iso.domain.enums

/**
 * @Author: ifechukwu.udorji
 * @Date: 6/3/2024
 */
enum class ISOAccountType(val value: String) {
    DEFAULT_ACCOUNT_TYPE("00"),
    SAVINGS("10"),
    CURRENT("20"),
    CREDIT("30"),
    UNIVERSAL_ACCOUNT("40"),
    INVESTMENT_ACCOUNT("50")
}