package com.dspreadbaseapp.baseapp.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.dspreadbaseapp.baseapp.data.local.DSpreadBaseAppDatabase
import com.basepos.pos.host.iso.data.local.ProcessedTransactionDao
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
interface AppModule {

    companion object {
        @Provides
        fun provideContext(@ApplicationContext context: Context): Context {
            return context
        }

        @Provides
        fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
            return context.getSharedPreferences("BasePosAgentPref", Context.MODE_PRIVATE)
        }

        @Provides
        fun provideBasePosDatabase(@ApplicationContext context: Context): DSpreadBaseAppDatabase {
            return Room.databaseBuilder(context, DSpreadBaseAppDatabase::class.java, "BasePosAgentDb")
                .fallbackToDestructiveMigration()
                .build()
        }

        @Provides
        fun provideProcessedTransactionDao(appDatabase: DSpreadBaseAppDatabase): ProcessedTransactionDao {
            return appDatabase.processedTransactionDao()
        }

//        @Provides
//        fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
//            return WorkManager.getInstance(context)
//        }
    }
}