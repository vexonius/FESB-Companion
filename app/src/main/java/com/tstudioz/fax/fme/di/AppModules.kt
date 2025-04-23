package com.tstudioz.fax.fme.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.tstudioz.fax.fme.database.AppDatabase
import com.tstudioz.fax.fme.feature.settings.SettingsViewModel
import com.tstudioz.fax.fme.feature.timetable.view.TimetableViewModel
import com.tstudioz.fax.fme.networking.NetworkUtils
import com.tstudioz.fax.fme.networking.cookies.MonsterCookieJar
import com.tstudioz.fax.fme.networking.interceptors.FESBLoginInterceptor
import com.tstudioz.fax.fme.networking.session.SessionDelegate
import com.tstudioz.fax.fme.networking.session.SessionDelegateInterface
import com.tstudioz.fax.fme.routing.AppRouter
import com.tstudioz.fax.fme.routing.HomeRouter
import com.tstudioz.fax.fme.routing.LoginRouter
import com.tstudioz.fax.fme.routing.Router
import com.tstudioz.fax.fme.routing.SettingsRouter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.binds
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
@InternalCoroutinesApi
val module = module {
    single { Router() } binds arrayOf(LoginRouter::class, SettingsRouter::class, HomeRouter::class, AppRouter::class)
    single { NetworkUtils(androidContext()) }
    single { MonsterCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(androidContext())) }
    single<FESBLoginInterceptor>(named("FESBInterceptor")) { FESBLoginInterceptor(get(), get(), get()) }
    single<OkHttpClient> { provideOkHttpClient(get()) }
    single<OkHttpClient>(named("FESBPortalClient")) { provideFESBPortalClient(get(), get(named("FESBInterceptor"))) }
    single<SessionDelegateInterface> { SessionDelegate(get(), get()) }
    factory<AppDatabase> { getRoomDatabase(get()) }
    single<SharedPreferences> { getSharedPreferences(androidContext()) }
    viewModel { TimetableViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel(androidApplication(), get()) }
}

fun getRoomDatabase(application: Application): AppDatabase {
    return Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        DATABASE_NAME
    ).build()
}

fun provideOkHttpClient(monsterCookieJar: MonsterCookieJar): OkHttpClient {
    return OkHttpClient.Builder()
        .callTimeout(15, TimeUnit.SECONDS)
        .connectTimeout(15, TimeUnit.SECONDS)
        .cookieJar(monsterCookieJar)
        .build()
}

fun provideFESBPortalClient(
    monsterCookieJar: MonsterCookieJar,
    interceptor: FESBLoginInterceptor,
): OkHttpClient {
    return OkHttpClient.Builder()
        .callTimeout(15, TimeUnit.SECONDS)
        .connectTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(interceptor)
        .cookieJar(monsterCookieJar)
        .build()
}

fun getSharedPreferences(androidContext: Context): SharedPreferences {
    return androidContext.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
}

const val SHARED_PREFS_NAME = "shared_prefs"
const val DATABASE_NAME = "database-name"