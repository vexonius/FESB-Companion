package com.tstudioz.fax.fme.Application

import android.app.Application
import com.tstudioz.fax.fme.feature.login.di.loginModule
import com.tstudioz.fax.fme.feature.login.di.studomatModule
import com.tstudioz.fax.fme.models.di.module
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

@InternalCoroutinesApi
class FESBCompanion : Application() {

    override fun onCreate() {
        super.onCreate()

        instance = this

        startKoin {
            androidLogger(level = Level.ERROR)
            androidContext(this@FESBCompanion)
            modules(module, loginModule, studomatModule)
        }
    }

    companion object {
        @JvmStatic
        var instance: FESBCompanion? = null
            private set
    }
}