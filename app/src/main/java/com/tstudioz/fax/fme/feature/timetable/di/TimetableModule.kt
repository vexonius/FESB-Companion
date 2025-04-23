package com.tstudioz.fax.fme.feature.timetable.di

import com.tstudioz.fax.fme.database.AppDatabase
import com.tstudioz.fax.fme.feature.timetable.dao.TimeTableDao
import com.tstudioz.fax.fme.feature.timetable.repository.TimeTableRepository
import com.tstudioz.fax.fme.feature.timetable.repository.interfaces.TimeTableRepositoryInterface
import com.tstudioz.fax.fme.feature.timetable.services.TimetableService
import com.tstudioz.fax.fme.feature.timetable.services.interfaces.TimetableServiceInterface
import org.koin.core.qualifier.named
import org.koin.dsl.module

val timetableModule = module {
    single<TimetableServiceInterface> { TimetableService(get(named("FESBPortalClient"))) }
    single<TimeTableDao> { getTimeTableDao(get()) }
    single<TimeTableRepositoryInterface> { TimeTableRepository(get(), get()) }
}

fun getTimeTableDao(db: AppDatabase): TimeTableDao {
    return db.timetableDao()
}