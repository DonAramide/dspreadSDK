package com.basepos.pos.dspread.di

import android.content.Context
import com.basepos.pos.common.domain.printer.PrinterEngine
import com.basepos.pos.common.domain.repository.EmvListener
import com.basepos.pos.common.domain.repository.SessionManager
import com.basepos.pos.common.utils.FileDownloadUtils
import com.basepos.pos.dspread.DSpreadEmvListener
import com.basepos.pos.dspread.MyQposClass
import com.basepos.pos.dspread.printer.DSpreadPrinterEngine
import dagger.Binds
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
interface DSpreadModule {
    @Binds
    fun bindPrinterEngine(dspreadPrinterEngine: DSpreadPrinterEngine): PrinterEngine

    @Binds
    fun bindTopwiseEmvListener(dspreadEmvListener: DSpreadEmvListener): EmvListener

    companion object {
        @Provides
        fun provideDSpreadEmvListener(
            @ApplicationContext context: Context,
            sessionManager: SessionManager,
            qposClass: MyQposClass
        ): DSpreadEmvListener {
            return DSpreadEmvListener(
                context,
                sessionManager,
                Dispatchers.IO,
                qposClass
            )
        }

        @Provides
        fun provideDSpreadPrinter(
            @ApplicationContext context: Context,
            fileDownloadUtils: FileDownloadUtils
        ): DSpreadPrinterEngine {
            return DSpreadPrinterEngine(
                context,
                Dispatchers.IO,
                fileDownloadUtils
            )
        }

        @Provides
        fun provideQPosClass(): MyQposClass = MyQposClass()
    }
}