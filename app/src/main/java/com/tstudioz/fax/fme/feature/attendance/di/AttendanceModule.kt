package com.tstudioz.fax.fme.feature.attendance.di

import android.app.Application
import androidx.room.Room
import com.tstudioz.fax.fme.database.AppDatabase
import com.tstudioz.fax.fme.feature.attendance.dao.AttendanceDao
import com.tstudioz.fax.fme.feature.attendance.repository.AttendanceRepository
import com.tstudioz.fax.fme.feature.attendance.repository.AttendanceRepositoryInterface
import com.tstudioz.fax.fme.feature.attendance.services.AttendanceService
import com.tstudioz.fax.fme.feature.attendance.services.AttendanceServiceInterface
import com.tstudioz.fax.fme.feature.attendance.view.AttendanceViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
val attendanceModule = module {
    single<AttendanceServiceInterface> { AttendanceService(get(named("FESBPortalClient"))) }
    factory<AppDatabase> { getRoomDatabase(get()) }
    single<AttendanceDao>{ getAttendanceDao(get()) }
    single<AttendanceRepositoryInterface> { AttendanceRepository(get(), get()) }
    viewModel { AttendanceViewModel(get()) }
}


fun getRoomDatabase(application: Application): AppDatabase {
    return Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "database-name"
    ).build()
}

fun getAttendanceDao(db:AppDatabase): AttendanceDao {
    return db.attendanceDaoRoom()
}