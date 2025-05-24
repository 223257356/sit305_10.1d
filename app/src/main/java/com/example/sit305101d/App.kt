package com.example.sit305101d

import android.app.Application
import com.example.sit305101d.di.AppModule
import com.tencent.mmkv.MMKV
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        startKoin {
            androidContext(this@App)
            androidLogger()
            modules(listOf(AppModule.module))
        }
    }
}
