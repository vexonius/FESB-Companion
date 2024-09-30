package com.tstudioz.fax.fme.models.data

import com.tstudioz.fax.fme.database.models.AttendanceEntry
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.feature.home.WeatherFeature
import java.time.LocalDate

interface UserRepositoryInterface {

    suspend fun attemptLogin(user: User): User
    suspend fun fetchTimetable(user: String, startDate: String, endDate: String): List<Event>
    suspend fun insertTimeTable(classes: List<Event>)
    suspend fun fetchWeatherDetails(): WeatherFeature?
    suspend fun fetchAttendance(user: User): NetworkServiceResult.AttendanceFetchResult
    suspend fun insertAttendance(attendance: List<AttendanceEntry>)

}