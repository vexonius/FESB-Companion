package com.tstudioz.fax.fme.Application

import android.app.Application
import android.content.SharedPreferences
import com.tstudioz.fax.fme.di.module
import com.tstudioz.fax.fme.feature.attendance.di.attendanceModule
import com.tstudioz.fax.fme.feature.home.di.homeModule
import com.tstudioz.fax.fme.feature.iksica.di.iksicaModule
import com.tstudioz.fax.fme.feature.login.di.loginModule
import com.tstudioz.fax.fme.feature.menza.di.menzaModule
import com.tstudioz.fax.fme.feature.studomat.di.studomatModule
import com.tstudioz.fax.fme.feature.timetable.di.timetableModule
import com.tstudioz.fax.fme.networking.session.SessionDelegateInterface
import com.tstudioz.fax.fme.routing.AppRouter
import com.tstudioz.fax.fme.util.SPKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import com.tstudioz.fax.fme.util.PreferenceHelper.get
import com.tstudioz.fax.fme.util.PreferenceHelper.set
import com.tstudioz.fax.fme.util.contains

@InternalCoroutinesApi
class FESBCompanion : Application() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    val sharedPreferences: SharedPreferences by inject()

    override fun onCreate() {
        super.onCreate()

        instance = this

        startKoin {
            androidLogger(level = Level.ERROR)
            androidContext(this@FESBCompanion)
            modules(
                module,
                attendanceModule,
                iksicaModule,
                loginModule,
                homeModule,
                menzaModule,
                timetableModule,
                studomatModule
            )
        }

        observeUserDeleted()
        setInitialGlowState()
    }

    private fun setInitialGlowState() {
        if (!sharedPreferences.contains(SPKey.EVENTS_GLOW)) {
            sharedPreferences[SPKey.EVENTS_GLOW] = false
        }
    }

    private fun observeUserDeleted() {
        val sessionDelegate: SessionDelegateInterface by inject()
        val router: AppRouter by inject()

        scope.launch(Dispatchers.Main) {
            sessionDelegate.onUserDeleted
                .collect {
                    if (sharedPreferences[SPKey.LOGGED_IN, false]) {
                        router.routeToLogin()
                    }
                }
        }
    }

    companion object {
        @JvmStatic
        var instance: FESBCompanion? = null
            private set
    }
}