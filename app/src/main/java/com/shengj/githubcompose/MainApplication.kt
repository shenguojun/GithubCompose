package com.shengj.githubcompose

import android.app.Application
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree

/**
 * Application that sets up Timber in the DEBUG BuildConfig.
 */
@HiltAndroidApp
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        if (BuildConfig.DEBUG) Timber.plant(DebugTree())
    }
}