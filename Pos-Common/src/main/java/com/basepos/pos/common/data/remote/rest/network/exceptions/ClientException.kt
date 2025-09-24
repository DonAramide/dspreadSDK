package com.basepos.pos.common.data.remote.rest.network.exceptions

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/6/2024
 */

/**
 * Represents general http 400 error
 */
class ClientException(message: String?, val code: Int) : Exception(message)
