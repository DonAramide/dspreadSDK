package com.basepos.pos.common.data.remote.rest.api

import com.basepos.pos.common.BuildConfig
import com.basepos.pos.common.data.remote.rest.interceptor.HeaderInterceptor
import com.basepos.pos.common.data.remote.rest.interceptor.TimeoutInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/6/2024
 */
object RetrofitClient {
    private val isDebugEnabled = BuildConfig.DEBUG

    private val httpClientBuilder: OkHttpClient.Builder
        get() {
            val httpClient = OkHttpClient.Builder()

            if (isDebugEnabled) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                httpClient.addInterceptor(logging)
            }

            httpClient.addInterceptor(TimeoutInterceptor())
            httpClient.networkInterceptors().add(HeaderInterceptor())
            return httpClient
        }

    fun getRetrofitClient(apiBaseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .client(httpClientBuilder.build())
            .build()
    }
}