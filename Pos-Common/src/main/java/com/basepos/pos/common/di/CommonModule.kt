package com.basepos.pos.common.di

import android.content.Context
import com.basepos.pos.common.domain.printer.PrinterHelper
import com.basepos.pos.common.domain.repository.SessionManager
import com.basepos.pos.common.utils.FileDownloadUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/3/2024
 */
@Module
@InstallIn(SingletonComponent::class)
object CommonModule {

    @Provides
    fun provideFileDownloadUtils(
        @ApplicationContext context: Context
    ): FileDownloadUtils {
        return FileDownloadUtils(context, Dispatchers.IO)
    }

    @Provides
    fun providePrinterHelper(@ApplicationContext context: Context, sessionManager: SessionManager): PrinterHelper {
        return PrinterHelper(context, sessionManager)
    }
}