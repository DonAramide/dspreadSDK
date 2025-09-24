package com.basepos.pos.common.data.remote.rest.network.exceptions

import java.net.HttpURLConnection

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/6/2024
 */


/**
 * Exception used to identify 500 kind of error
 */
class ServerException(message: String, val code: Int = HttpURLConnection.HTTP_INTERNAL_ERROR) : Exception(message)
