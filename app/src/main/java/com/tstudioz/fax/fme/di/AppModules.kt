package com.tstudioz.fax.fme.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.CookieCache
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.CookiePersistor
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.tstudioz.fax.fme.database.DatabaseManager
import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.feature.attendance.dao.AttendanceDao
import com.tstudioz.fax.fme.feature.attendance.dao.AttendanceDaoInterface
import com.tstudioz.fax.fme.feature.attendance.repository.AttendanceRepository
import com.tstudioz.fax.fme.feature.attendance.repository.AttendanceRepositoryInterface
import com.tstudioz.fax.fme.feature.attendance.services.AttendanceServiceInterface
import com.tstudioz.fax.fme.feature.attendance.services.AttendanceService
import com.tstudioz.fax.fme.feature.attendance.view.AttendanceViewModel
import com.tstudioz.fax.fme.networking.clients.FESBPortalClient
import com.tstudioz.fax.fme.networking.cookies.MonsterCookieJar
import com.tstudioz.fax.fme.networking.interceptors.FESBLoginInterceptor
import com.tstudioz.fax.fme.networking.session.SessionDelegate
import com.tstudioz.fax.fme.networking.session.SessionDelegateInterface
import com.tstudioz.fax.fme.random.NetworkUtils
import com.tstudioz.fax.fme.viewmodel.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
@InternalCoroutinesApi
val module = module {
    single { NetworkUtils(androidContext()) }
    single { MonsterCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(androidContext())) }
    single<Interceptor> { FESBLoginInterceptor(get(), get(), get()) }
    single<OkHttpClient> { provideOkHttpClient(get()) }
    single<OkHttpClient>(named("FESBPortalClient")) { provideFESBPortalClient(get(), get()) }
    single<SessionDelegateInterface> { SessionDelegate(get()) }
    single<DatabaseManagerInterface> { DatabaseManager() }
    single<AttendanceServiceInterface> { AttendanceService(get(named("FESBPortalClient"))) }
    single<AttendanceDaoInterface> { AttendanceDao(get()) }
    single<AttendanceRepositoryInterface> { AttendanceRepository(get(), get()) }
    single <SharedPreferences> { encryptedSharedPreferences(androidContext()) }
    viewModel { MainViewModel(get(), get()) }
    viewModel { AttendanceViewModel(get(), get()) }
}

fun provideOkHttpClient(monsterCookieJar: MonsterCookieJar) : OkHttpClient {
    return OkHttpClient.Builder()
            .callTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .cookieJar(monsterCookieJar)
            .build()
}

fun provideFESBPortalClient(monsterCookieJar: MonsterCookieJar, FESBLoginInterceptor: Interceptor) : OkHttpClient {
    return OkHttpClient.Builder()
        .callTimeout(15, TimeUnit.SECONDS)
        .connectTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(FESBLoginInterceptor)
        .cookieJar(monsterCookieJar)
        .build()
}

fun encryptedSharedPreferences(androidContext: Context): SharedPreferences {
    val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    return EncryptedSharedPreferences.create(
        "PreferencesFilename",
        masterKeyAlias,
        androidContext,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}
