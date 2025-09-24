package com.basepos.pos.common.domain.enums

/**
 * @Author: ifechukwu.udorji
 * @Date: 4/24/2025
 */
enum class TerminalType(val code: String) {
    ATTENDED_ONLINE_OFFLINE("22"),
    ATTENDED_ONLINE_ONLY("21"),
    UNATTENDED_ONLINE_ONLY("24")
}