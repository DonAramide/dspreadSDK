package com.basepos.pos.common.domain.repository

import com.basepos.pos.common.domain.models.EmvResult
import com.basepos.pos.common.domain.models.PaymentRequest

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/18/2024
 */
interface EmvListener {
    suspend fun initSdk(): Boolean
    suspend fun startCardTransaction(paymentRequest: PaymentRequest, callback: (EmvResult) -> Unit)
    suspend fun injectPinpadKeys(): Boolean
    fun loadAidCapk()
    suspend fun closeSdk()
}