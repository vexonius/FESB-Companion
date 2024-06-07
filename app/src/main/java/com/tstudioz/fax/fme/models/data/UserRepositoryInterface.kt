package com.tstudioz.fax.fme.models.data

import com.tstudioz.fax.fme.database.models.Dolazak
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.feature.weather.WeatherFeature
import java.time.LocalDate

interface UserRepositoryInterface {

    suspend fun attemptLogin(user: User): User
    suspend fun fetchTimetable(user: String, startDate: LocalDate, endDate: LocalDate): List<Event>
    suspend fun insertTimeTable(classes: List<Event>)
    suspend fun fetchWeatherDetails(url : String): WeatherFeature?
    suspend fun fetchAttendance(user: User): NetworkServiceResult.PrisutnostResult
    suspend fun insertAttendance(attendance: List<Dolazak>)

}