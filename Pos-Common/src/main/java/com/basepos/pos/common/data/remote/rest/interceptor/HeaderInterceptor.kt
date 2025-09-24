package com.basepos.pos.common.data.remote.rest.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/6/2024
 */
class HeaderInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        request.header("Content-Type", "application/json")
        request.header("Connection", "keep-alive")

        return chain.proceed(request.build())
    }
}