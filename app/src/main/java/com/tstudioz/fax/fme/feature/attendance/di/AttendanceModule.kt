package com.tstudioz.fax.fme.feature.attendance.di

import com.tstudioz.fax.fme.feature.attendance.dao.AttendanceDao
import com.tstudioz.fax.fme.feature.attendance.dao.AttendanceDaoInterface
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
    single<AttendanceDaoInterface> { AttendanceDao(get()) }
    single<AttendanceRepositoryInterface> { AttendanceRepository(get(), get()) }
    viewModel { AttendanceViewModel(get()) }
}