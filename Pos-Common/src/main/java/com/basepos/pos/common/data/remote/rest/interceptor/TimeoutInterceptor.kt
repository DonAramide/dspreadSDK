package com.basepos.pos.common.data.remote.rest.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/6/2024
 */
class TimeoutInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val timeout = 60

        return chain
            .withConnectTimeout(timeout, TimeUnit.SECONDS)
            .withWriteTimeout(timeout, TimeUnit.SECONDS)
            .withReadTimeout(timeout, TimeUnit.SECONDS)
            .proceed(request)
    }
}