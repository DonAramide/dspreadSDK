package com.basepos.pos.common.domain.enums

/**
 * @Author: ifechukwu.udorji
 * @Date: 4/24/2025
 */
enum class TerminalCapability(val value: String) {
    ADDITIONAL_TERMINAL_CAPABILITY("7F00F0F001"),
    TERMINAL_CAPABILITY_CVM("E0F8C8"),
    TERMINAL_CAPABILITY_CVM_ONLY("E020C8"),
    TERMINAL_CAPABILITY_NO_CVM("E0DOC8")
}