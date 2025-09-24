package com.dspreadbaseapp.baseapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.basepos.pos.host.iso.data.local.ProcessedTransactionDao
import com.basepos.pos.host.iso.data.local.converter.Converter
import com.basepos.pos.host.iso.data.local.entities.ProcessedTransactionEntity

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/3/2024
 */
@Database(
    entities = [
        ProcessedTransactionEntity::class,
    ], version = 1,
    exportSchema = false
)
@TypeConverters(Converter::class)
abstract class DSpreadBaseAppDatabase: RoomDatabase() {
    abstract fun processedTransactionDao(): ProcessedTransactionDao
}