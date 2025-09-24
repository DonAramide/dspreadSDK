package com.basepos.pos.host.iso.di

import android.content.Context
import com.basepos.pos.common.data.remote.rest.api.RetrofitClient
import com.basepos.pos.common.domain.repository.SessionManager
import com.basepos.pos.host.iso.data.local.ProcessedTransactionDao
import com.basepos.pos.host.iso.data.remote.rest.api.PosHostApiClient
import com.basepos.pos.host.iso.data.remote.socket.SocketChannel
import com.basepos.pos.host.iso.transaction.IsoMessageBuilder
import com.basepos.pos.host.iso.transaction.IsoPackager
import com.basepos.pos.host.iso.transaction.KeyExchangeHandler
import com.basepos.pos.host.iso.transaction.TransactionHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/3/2024
 */
@Module
@InstallIn(SingletonComponent::class)
object HostModule {

    @Provides
    fun provideNibbsPackager(): IsoPackager {
        return IsoPackager()
    }

    @Provides
    fun provideIsoMessageBuilder(
        sessionManager: SessionManager,
        isoPackager: IsoPackager
    ): IsoMessageBuilder {
        return IsoMessageBuilder(sessionManager, isoPackager)
    }

    @Provides
    fun provideKeyExchangeHandler(
        @ApplicationContext context: Context,
        sessionManager: SessionManager,
        socketChannel: SocketChannel,
        isoMessageBuilder: IsoMessageBuilder,
        posHostApiClient: PosHostApiClient
    ): KeyExchangeHandler {
        return KeyExchangeHandler(
            context,
            sessionManager,
            socketChannel,
            isoMessageBuilder,
            posHostApiClient
        )
    }

    @Provides
    fun provideTransactionHandler(
        @ApplicationContext context: Context,
        sessionManager: SessionManager,
        socketChannel: SocketChannel,
        isoMessageBuilder: IsoMessageBuilder,
        transactionDao: ProcessedTransactionDao
    ): TransactionHandler {
        return TransactionHandler(
            context,
            sessionManager,
            socketChannel,
            isoMessageBuilder,
            transactionDao
        )
    }

    @Provides
    fun providePosHostApiClient(): PosHostApiClient {
        return RetrofitClient
            .getRetrofitClient("http://80.88.8.56:552/")
            .create(PosHostApiClient::class.java)
    }
}