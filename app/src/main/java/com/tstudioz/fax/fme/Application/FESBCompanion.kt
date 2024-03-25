package com.tstudioz.fax.fme.Application

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.multidex.MultiDex
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.orhanobut.hawk.Hawk
import com.tstudioz.fax.fme.database.models.Korisnik
import com.tstudioz.fax.fme.models.di.module
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import java.security.SecureRandom

@InternalCoroutinesApi
class FESBCompanion : Application() {

    override fun onCreate() {
        super.onCreate()

        instance = this

        startKoin {
            androidLogger(level = Level.ERROR)
            androidContext(this@FESBCompanion)
            modules(module)
        }
    }

    val sP: SharedPreferences?
        get() {
            if (shPref == null) shPref = getSharedPreferences("PRIVATE_PREFS", Context.MODE_PRIVATE)
            return shPref
        }

    companion object {
        @JvmStatic
        var instance: FESBCompanion? = null
            private set
        private var shPref: SharedPreferences? = null
    }
}