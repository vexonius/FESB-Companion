package com.tstudioz.fax.fme.models.data

import com.tstudioz.fax.fme.database.models.Predavanja

interface TimeTableRepositoryInterface {

    suspend fun fetchTimetable(user: String, startDate: String, endDate: String): List<TimetableItem>
    suspend fun insertTimeTable(classes: List<Predavanja>)

}