package com.basepos.pos.ui.printer

import com.basepos.pos.common.domain.models.TransactionResponse

sealed class PrintState {
    data object Idle: PrintState()
    data object Printing: PrintState()
    data class PrintError(val message: String, val receiptType: String, val transactionResponse: TransactionResponse): PrintState()
    data class PrintMerchantCopy(val transactionResponse: TransactionResponse): PrintState()
    data class PrintDone(val transactionResponse: TransactionResponse): PrintState()
}