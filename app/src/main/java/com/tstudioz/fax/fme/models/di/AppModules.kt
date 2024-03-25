package com.tstudioz.fax.fme.models.di

import android.content.Context
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.tstudioz.fax.fme.database.DatabaseManager
import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.models.data.AttendanceDao
import com.tstudioz.fax.fme.models.data.AttendanceDaoInterface
import com.tstudioz.fax.fme.models.data.TimeTableDao
import com.tstudioz.fax.fme.models.data.TimeTableDaoInterface
import com.tstudioz.fax.fme.models.data.UserRepository
import com.tstudioz.fax.fme.models.data.UserRepositoryInterface
import com.tstudioz.fax.fme.models.interfaces.AttendanceServiceInterface
import com.tstudioz.fax.fme.models.interfaces.TimetableServiceInterface
import com.tstudioz.fax.fme.models.interfaces.UserServiceInterface
import com.tstudioz.fax.fme.models.interfaces.WeatherNetworkInterface
import com.tstudioz.fax.fme.models.services.AttendanceService
import com.tstudioz.fax.fme.models.services.TimetableService
import com.tstudioz.fax.fme.models.services.UserService
import com.tstudioz.fax.fme.models.services.WeatherNetworkService
import com.tstudioz.fax.fme.viewmodel.HomeViewModel
import com.tstudioz.fax.fme.viewmodel.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
@InternalCoroutinesApi
val module = module {

    single<UserRepositoryInterface> { UserRepository(get(), get(), get(), get(), get(), get()) }
    single<UserServiceInterface> { UserService(get()) }
    single<TimetableServiceInterface> { TimetableService(get()) }
    single<WeatherNetworkInterface> { WeatherNetworkService(get()) }
    single<AttendanceServiceInterface> { AttendanceService(get()) }
    single { provideOkHttpClient(androidContext()) }
    single<DatabaseManagerInterface> { DatabaseManager() }
    single<AttendanceDaoInterface> { AttendanceDao(get()) }
    single<TimeTableDaoInterface> { TimeTableDao(get()) }
    viewModel { MainViewModel(get()) }
    viewModel { HomeViewModel(androidApplication(), get()) }
}

fun provideOkHttpClient(context: Context) : OkHttpClient {
    return OkHttpClient.Builder()
            .callTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .cookieJar(PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context)))
            .build()
}
