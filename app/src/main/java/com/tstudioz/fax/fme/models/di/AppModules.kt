package com.tstudioz.fax.fme.models.di

import android.content.Context
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.tstudioz.fax.fme.database.DatabaseManager
import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.feature.home.dao.NoteDao
import com.tstudioz.fax.fme.feature.home.repository.NoteRepository
import com.tstudioz.fax.fme.feature.home.repository.NoteRepositoryInterface
import com.tstudioz.fax.fme.feature.menza.service.MenzaServiceInterface
import com.tstudioz.fax.fme.feature.menza.service.MenzaService
import com.tstudioz.fax.fme.feature.menza.view.MenzaViewModel
import com.tstudioz.fax.fme.models.data.AttendanceDao
import com.tstudioz.fax.fme.models.data.AttendanceDaoInterface
import com.tstudioz.fax.fme.models.data.AttendanceRepository
import com.tstudioz.fax.fme.models.data.AttendanceRepositoryInterface
import com.tstudioz.fax.fme.feature.menza.dao.MenzaDao
import com.tstudioz.fax.fme.feature.menza.dao.interfaces.MenzaDaoInterface
import com.tstudioz.fax.fme.feature.timetable.dao.TimeTableDao
import com.tstudioz.fax.fme.feature.timetable.dao.interfaces.TimeTableDaoInterface
import com.tstudioz.fax.fme.feature.timetable.repository.TimeTableRepository
import com.tstudioz.fax.fme.feature.timetable.repository.interfaces.TimeTableRepositoryInterface
import com.tstudioz.fax.fme.models.interfaces.AttendanceServiceInterface
import com.tstudioz.fax.fme.feature.timetable.services.interfaces.TimetableServiceInterface
import com.tstudioz.fax.fme.feature.home.services.WeatherServiceInterface
import com.tstudioz.fax.fme.models.services.AttendanceService
import com.tstudioz.fax.fme.feature.timetable.services.TimetableService
import com.tstudioz.fax.fme.feature.home.services.WeatherService
import com.tstudioz.fax.fme.viewmodel.AttendanceViewModel
import com.tstudioz.fax.fme.feature.home.view.HomeViewModel
import com.tstudioz.fax.fme.feature.menza.dao.interfaces.NoteDaoInterface
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
    single { provideOkHttpClient(androidContext()) }
    single { androidContext().getSharedPreferences("PRIVATE_PREFS", Context.MODE_PRIVATE) }
    single<DatabaseManagerInterface> { DatabaseManager() }
    single<AttendanceServiceInterface> { AttendanceService(get()) }
    single<AttendanceDaoInterface> { AttendanceDao(get()) }
    single<AttendanceRepositoryInterface> { AttendanceRepository(get(), get()) }
    viewModel { MainViewModel(get(), get(), get(), get()) }
    viewModel { AttendanceViewModel(get()) }
}

fun provideOkHttpClient(context: Context) : OkHttpClient {
    return OkHttpClient.Builder()
            .callTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .cookieJar(PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context)))
            .build()
}
