package com.basepos.pos.common.domain.printer

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/19/2024
 */

sealed class PrinterState {
    data object Printing : PrinterState()
    data class Error(val message: String) : PrinterState()
    data class Done(var printMerchant: Boolean = false) : PrinterState()
}