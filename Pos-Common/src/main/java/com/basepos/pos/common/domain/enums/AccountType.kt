package com.basepos.pos.common.domain.enums

/**
 * @Author: ifechukwu.udorji
 * @Date: 4/24/2025
 */
enum class AccountType(val code: String) {
    SAVINGS("10"),
    CURRENT("20"),
    CREDIT("30"),
    DEFAULT("00");
}