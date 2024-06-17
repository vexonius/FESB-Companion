package com.tstudioz.fax.fme.feature.timetable.di

import com.tstudioz.fax.fme.feature.timetable.dao.TimeTableDao
import com.tstudioz.fax.fme.feature.timetable.dao.interfaces.TimeTableDaoInterface
import com.tstudioz.fax.fme.feature.timetable.repository.TimeTableRepository
import com.tstudioz.fax.fme.feature.timetable.repository.interfaces.TimeTableRepositoryInterface
import com.tstudioz.fax.fme.feature.timetable.services.TimetableService
import com.tstudioz.fax.fme.feature.timetable.services.interfaces.TimetableServiceInterface
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.dsl.module

val timetableModule = module {
    single<TimetableServiceInterface> { TimetableService(get()) }
    single<TimeTableDaoInterface> { TimeTableDao(get()) }
    single<TimeTableRepositoryInterface> { TimeTableRepository(get(), get()) }
}