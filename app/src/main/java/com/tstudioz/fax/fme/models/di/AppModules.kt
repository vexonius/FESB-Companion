package com.tstudioz.fax.fme.models.di

import android.content.Context
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
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
import com.tstudioz.fax.fme.viewmodel.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
@InternalCoroutinesApi
val module = module {
    single { provideOkHttpClient(androidContext()) }
    single { androidContext().getSharedPreferences("PRIVATE_PREFS", Context.MODE_PRIVATE) }
    single<DatabaseManagerInterface> { DatabaseManager() }
    single<AttendanceServiceInterface> { AttendanceService(get()) }
    single<AttendanceDaoInterface> { AttendanceDao(get()) }
    single<AttendanceRepositoryInterface> { AttendanceRepository(get(), get()) }
    viewModel { MainViewModel(get(), get(), get(), get()) }
    viewModel { AttendanceViewModel(get(), get()) }
}

fun provideOkHttpClient(context: Context) : OkHttpClient {
    return OkHttpClient.Builder()
            .callTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .cookieJar(PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context)))
            .build()
}
