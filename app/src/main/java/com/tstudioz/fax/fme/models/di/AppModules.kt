package com.tstudioz.fax.fme.models.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.tstudioz.fax.fme.database.DatabaseManager
import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.feature.login.services.UserService
import com.tstudioz.fax.fme.models.data.AttendanceDao
import com.tstudioz.fax.fme.models.data.AttendanceDaoInterface
import com.tstudioz.fax.fme.models.data.AttendanceRepository
import com.tstudioz.fax.fme.models.data.AttendanceRepositoryInterface
import com.tstudioz.fax.fme.models.data.TimeTableDao
import com.tstudioz.fax.fme.models.data.TimeTableDaoInterface
import com.tstudioz.fax.fme.models.data.TimeTableRepository
import com.tstudioz.fax.fme.models.data.TimeTableRepositoryInterface
import com.tstudioz.fax.fme.models.interfaces.AttendanceServiceInterface
import com.tstudioz.fax.fme.models.interfaces.TimetableServiceInterface
import com.tstudioz.fax.fme.models.interfaces.WeatherNetworkInterface
import com.tstudioz.fax.fme.models.services.AttendanceService
import com.tstudioz.fax.fme.models.services.TimetableService
import com.tstudioz.fax.fme.models.services.WeatherNetworkService
import com.tstudioz.fax.fme.viewmodel.AttendanceViewModel
import com.tstudioz.fax.fme.viewmodel.HomeViewModel
import com.tstudioz.fax.fme.viewmodel.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
@InternalCoroutinesApi
val module = module {
    single<TimetableServiceInterface> { TimetableService(get()) }
    single<WeatherNetworkInterface> { WeatherNetworkService(get()) }
    single<AttendanceServiceInterface> { AttendanceService(get()) }
    single { provideOkHttpClient(androidContext()) }
    single<DatabaseManagerInterface> { DatabaseManager() }
    single<AttendanceDaoInterface> { AttendanceDao(get()) }
    single<TimeTableDaoInterface> { TimeTableDao(get()) }
    single<TimeTableRepositoryInterface> { TimeTableRepository(get(), get()) }
    single<AttendanceRepositoryInterface> { AttendanceRepository(get(), get()) }
    single <SharedPreferences> { encryptedSharedPreferences(androidContext()) }
    viewModel { MainViewModel(get(), get(), get(), get()) }
    viewModel { HomeViewModel(androidApplication(), get()) }
    viewModel { AttendanceViewModel(get()) }
}

fun provideOkHttpClient(context: Context) : OkHttpClient {
    return OkHttpClient.Builder()
            .callTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .cookieJar(PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context)))
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
