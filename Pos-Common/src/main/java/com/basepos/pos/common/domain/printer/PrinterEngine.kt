package com.basepos.pos.common.domain.printer

import com.basepos.pos.common.domain.models.ReceiptFields
import kotlinx.coroutines.flow.Flow

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/19/2024
 */
interface PrinterEngine {
    suspend fun printReceipt(receipt: ReceiptFields, isEod: Boolean = false): Flow<PrinterState>
}