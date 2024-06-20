package com.tstudioz.fax.fme.models.di

import android.content.Context
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.tstudioz.fax.fme.database.DatabaseManager
import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.models.data.AttendanceDao
import com.tstudioz.fax.fme.models.data.AttendanceDaoInterface
import com.tstudioz.fax.fme.models.data.AttendanceRepository
import com.tstudioz.fax.fme.models.data.AttendanceRepositoryInterface
import com.tstudioz.fax.fme.models.data.IksicaRepository
import com.tstudioz.fax.fme.models.data.IksicaRepositoryInterface
import com.tstudioz.fax.fme.models.data.TimeTableDao
import com.tstudioz.fax.fme.models.data.TimeTableDaoInterface
import com.tstudioz.fax.fme.models.interfaces.AttendanceServiceInterface
import com.tstudioz.fax.fme.models.interfaces.IksicaServiceInterface
import com.tstudioz.fax.fme.models.interfaces.TimetableServiceInterface
import com.tstudioz.fax.fme.models.interfaces.WeatherNetworkInterface
import com.tstudioz.fax.fme.models.services.AttendanceService
import com.tstudioz.fax.fme.models.services.IksicaService
import com.tstudioz.fax.fme.models.services.TimetableService
import com.tstudioz.fax.fme.models.services.WeatherNetworkService
import com.tstudioz.fax.fme.viewmodel.AttendanceViewModel
import com.tstudioz.fax.fme.viewmodel.HomeViewModel
import com.tstudioz.fax.fme.viewmodel.IksicaViewModel
import com.tstudioz.fax.fme.viewmodel.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
@InternalCoroutinesApi
val module = module {
    single<TimetableServiceInterface> { TimetableService(get()) }
    single<IksicaServiceInterface> { IksicaService(get(),get(named("client2"))) }
    single <IksicaRepositoryInterface> { IksicaRepository(get()) }
    single<WeatherNetworkInterface> { WeatherNetworkService(get()) }
    single<AttendanceServiceInterface> { AttendanceService(get()) }
    single { provideOkHttpClient(androidContext()) }
    single(named("client2")) { provideOkHttpClient2() }
    single<DatabaseManagerInterface> { DatabaseManager() }
    single<AttendanceDaoInterface> { AttendanceDao(get()) }
    single<TimeTableDaoInterface> { TimeTableDao(get()) }
    single<TimeTableRepositoryInterface> { TimeTableRepository(get(), get()) }
    single<AttendanceRepositoryInterface> { AttendanceRepository(get(), get()) }
    single { androidContext().getSharedPreferences("PRIVATE_PREFS", Context.MODE_PRIVATE) }
    single { IksicaViewModel(androidApplication(), get()) }
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
fun provideOkHttpClient2() : OkHttpClient {
    return OkHttpClient.Builder()
        .build()
}
