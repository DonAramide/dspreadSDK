package com.basepos.pos.common.domain.models

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/3/2024
 */
sealed class EmvResult {
    data object Idle : EmvResult()
    data class Error(val message: String) : EmvResult()
    data class Loading(val message: String) : EmvResult()
    data class OnPinInput(val pinLength: Int) : EmvResult()
    data class OnPinInputRequired(
        val isOnlinePin: Boolean,
        val offlinePinTrialCount: Int
    ) : EmvResult()

    data class OnRequestOnline(val emvTransactionDetails: EmvTransactionDetails) : EmvResult()
}