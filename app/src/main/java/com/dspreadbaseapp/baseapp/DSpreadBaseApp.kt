package com.dspreadbaseapp.baseapp

import android.app.Application
import com.basepos.pos.common.domain.repository.EmvListener
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * @Author: ifechukwu.udorji
 * @Date: 4/19/2025
 */
@HiltAndroidApp
class DSpreadBaseApp: Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    @Inject
    lateinit var emvListener: EmvListener

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            //Todo -> Add crash reporting tree
        }

        applicationScope.launch(Dispatchers.IO) {
            if (emvListener.initSdk()) {
                Timber.d("SDK initialized successfully")
            } else {
                Timber.e("SDK initialization failed")
            }
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        applicationScope.launch(Dispatchers.IO) {
            emvListener.closeSdk()
        }
    }
}