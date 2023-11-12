package com.tstudioz.fax.fme.Application

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.multidex.MultiDex
import androidx.work.*
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.android.gms.ads.MobileAds
import com.orhanobut.hawk.Hawk
import com.tstudioz.fax.fme.di.module
import com.tstudioz.fax.fme.migrations.CredMigration
import com.tstudioz.fax.fme.workers.ScheduledWorker
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import java.io.File
import java.security.SecureRandom
import java.util.concurrent.TimeUnit


@InternalCoroutinesApi
class FESBCompanion : Application() {

    var CredRealmCf: RealmConfiguration? = null
    var sP: SharedPreferences? = null


    override fun onCreate() {
        super.onCreate()

        sP = this.getSharedPreferences("PRIVATE_PREFS", Context.MODE_PRIVATE)
        Hawk.init(this).build()

        instance = this

        Realm.init(this)

        CredRealmCf = RealmConfiguration.Builder()
                .name("encryptedv2.realm")
                .schemaVersion(7)
                .migration(CredMigration())
                .encryptionKey(realmKey)
                .build()

        Realm.setDefaultConfiguration(CredRealmCf)
        checkOldVersion()

        startKoin {
            androidLogger(level = Level.DEBUG)
            androidContext(this@FESBCompanion)
            modules(module)
        }

        MobileAds.initialize(this, "ca-app-pub-5944203368510130~8955475006")

        setupTimetableSyncWorker()
    }

    private fun setupTimetableSyncWorker() {
        val worker = WorkManager.getInstance(this)

        if (sP!!.getBoolean("timetable_sync_enabled", true)) {
            val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()

            val request = PeriodicWorkRequest
                    .Builder(ScheduledWorker::class.java, 25, TimeUnit.MINUTES)
                    .setConstraints(constraints)
                    .build()

            worker.enqueueUniquePeriodicWork("fc_timetable_sync", ExistingPeriodicWorkPolicy.KEEP, request)
        } else {
            worker.cancelUniqueWork("fc_timetable_sync")
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    fun checkOldVersion() {
        val newRealmFile = File(CredRealmCf!!.path)
        if (!newRealmFile.exists()) {
            // Migrate old Realm and delete old
            val old = RealmConfiguration.Builder()
                    .name("encrypted.realm")
                    .schemaVersion(7)
                    .migration(CredMigration())
                    .build()
            val realm = Realm.getInstance(old)
            realm.writeEncryptedCopyTo(newRealmFile, realmKey)
            realm.close()
            Realm.deleteRealm(old)
        }
    }

    private val realmKey: ByteArray
        private get() {
            if (Hawk.contains("masterKey")) {
                return Hawk.get("masterKey")
            }
            val bytes = ByteArray(64)
            SecureRandom().nextBytes(bytes)
            Hawk.put("masterKey", bytes)
            return bytes
        }


    val okHttpInstance: OkHttpClient?
        get() {
            if (okHttpClient == null) {
                val cookieJar: CookieJar = PersistentCookieJar(SetCookieCache(),
                                                               SharedPrefsCookiePersistor(applicationContext))
                okHttpClient = OkHttpClient().newBuilder()
                        .followRedirects(true)
                        .followSslRedirects(true)
                        .cookieJar(cookieJar)
                        .build()
            }
            return okHttpClient
        }

    companion object {
        private var okHttpClient: OkHttpClient? = null

        @JvmStatic
        var instance: FESBCompanion? = null
            private set
    }


}