package com.tstudioz.fax.fme.models.data

import com.tstudioz.fax.fme.database.models.Dolazak
import com.tstudioz.fax.fme.database.models.Predavanja
import com.tstudioz.fax.fme.models.Result
import com.tstudioz.fax.fme.weather.Current

interface UserRepositoryInterface {

    suspend fun attemptLogin(user: User): User
    suspend fun fetchTimetable(user: String, startDate: String, endDate: String): List<TimetableItem>
    suspend fun insertTimeTable(classes: List<Predavanja>)
    suspend fun fetchWeatherDetails(url : String): Current?
    suspend fun fetchAttendance(user: User): Result.PrisutnostResult
    suspend fun insertAttendance(attendance: List<Dolazak>)

}