package com.basepos.pos.common.domain.enums

/**
 * @Author: ifechukwu.udorji
 * @Date: 6/3/2024
 */
enum class TransactionType(val code: Int, val value: String) {
    PURCHASE(0x00, "PURCHASE"),
    BALANCE(0x31, "BALANCE"),
    REVERSAL(0x20, "REVERSAL"),
    AUTOREVERSAL(0x20, "AUTOREVERSAL"),
    PURCHASEWITHCB(0x09, "PURCHASE WITH CASHBACK"),
    CASHADVANCE(0x01, "CASH ADVANCE"),
    REFUND(0x20, "REFUND"),
    PREAUTH(0x60, "PRE-AUTH"),
    PREAUTHCOMPLETE(0x61, "PRE-AUTH COMPLETION");
}