package com.basepos.pos.common.utils

/**
 * @Author: ifechukwu.udorji
 * @Date: 6/3/2024
 */
object Constants {
    const val PARAM_PREF_KEY = "PARAM_PREF_KEY"
    const val PARAM_LAST_UPDATED_PREF_KEY = "PARAM_LAST_UPDATED_PREF_KEY"
    const val ISO_SUCCESS = "00"
    const val ISO_ERROR = "06"
    const val ISO_APPROVAL_PARTIAL = "10"
    const val ISO_APPROVED_VIP = "11"
    const val ISO_NOT_SUFFICIENT_FUNDS = "51"
    const val ISO_ISSUER_SWITCH_INOPERATIVE = "91"
    const val ISO_ROUTING_ERROR = "92"
    const val SIXTY_FOUR_ZEROS = "0000000000000000000000000000000000000000000000000000000000000000"
    const val POS_DATA_CODE = "51010151134C101"
    const val POS_DATA_CODE_CONTACTLESS_ICC = "A11101713344101"
    const val POS_CONDITION_CODE = "00"
    const val POS_PIN_CAPTURE_CODE = "12"
    const val REASON_TIMEOUT = "4021"
    const val REASON_CUSTOMER_CANCELLATION = "4000"

    const val PREF_TERMINAL_INFO = "PREF_TERMINAL_INFO"
    const val PREF_LATITUDE = "PREF_LATITUDE"
    const val PREF_LONGITUDE = "PREF_LONGITUDE"
    const val PREF_TERMINAL_SERIAL_NUMBER = "PREF_TERMINAL_SERIAL_NUMBER"
    const val PREF_TMS_PARAMETER = "PREF_TMS_PARAMETER"
    const val PREF_CTMK = "PREF_CTMK"

    const val TMS_ENABLE_SSL = "enable_ssl"
    const val TMS_ENABLE_BALANCE = "enable_balance"
    const val TMS_ENABLE_CONTACTLESS = "enable_contactless"
    const val TMS_ENABLE_CASH_ADVANCE = "enable_cash_advance"
    const val TMS_ENABLE_COMPLETION = "enable_completion"
    const val TMS_ENABLE_PRE_AUTH = "enable_pre_auth"
    const val TMS_ENABLE_PURCHASE = "enable_purchase"
    const val TMS_ENABLE_PURCHASE_CB = "enable_purchase_cb"
    const val TMS_ENABLE_REFUND = "enable_refund"
    const val TMS_ENABLE_REVERSAL = "enable_reversal"
    const val TMS_ADMIN_PASSWORD = "admin_password"
    const val TMS_FOOTER_MESSAGE = "footer_message"
    const val TMS_FAIL_OVER = "fail_over"
    const val TMS_ENABLE_SECONDARY_SSL = "enable_secondary_ssl"
    const val TMS_FAIL_OVER_CODE = "fail_over_code"
    const val TMS_PTSP = "ptsp"
    const val TMS_PTSP_CONTACT = "ptsp_contact"
    const val TMS_BANK_NAME = "bank_name"
    const val TMS_BANK_LOGO_URL = "bank_logo_url"
}