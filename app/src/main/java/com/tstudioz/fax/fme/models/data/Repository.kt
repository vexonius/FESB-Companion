package com.tstudioz.fax.fme.models.data

import android.util.Log
import com.tstudioz.fax.fme.database.models.Dolazak
import com.tstudioz.fax.fme.database.models.Predavanja
import com.tstudioz.fax.fme.models.Result
import com.tstudioz.fax.fme.models.interfaces.AttendanceServiceInterface
import com.tstudioz.fax.fme.models.services.TimetableNetworkService
import com.tstudioz.fax.fme.models.services.UserService
import com.tstudioz.fax.fme.models.services.WeatherNetworkService
import com.tstudioz.fax.fme.models.util.parseTimetable
import com.tstudioz.fax.fme.models.util.parseWeatherDetails
import com.tstudioz.fax.fme.weather.Current
import org.koin.core.KoinComponent
import org.koin.core.inject

class Repository: KoinComponent {

    private val service: UserService by inject()
    private val timetableNetworkService: TimetableNetworkService by inject()
    private val weatherNetworkService: WeatherNetworkService by inject()
    private val attendanceService: AttendanceServiceInterface by inject()
    private val timeTableDao: TimeTableDao by inject()
    private val attendanceDao: AttendanceDao by inject()

    suspend fun attemptLogin(user: User): User {
        return when (val result = service.loginUser(user)) {
            is Result.LoginResult.Success -> (result.data)
            is Result.LoginResult.Failure -> {
                Log.d(TAG, "Doslo je do pogreske")
                (User("","",""))
            }
        }
    }

    suspend fun fetchTimetable(user: String, startDate: String, endDate: String): List<TimetableItem> {
        return when(val result = timetableNetworkService.fetchTimeTable(user, startDate, endDate)){
            is Result.TimeTableResult.Success -> parseTimetable(result.data)
            is Result.TimeTableResult.Failure -> {
                Log.e(TAG, "Timetable fetching error")
                throw Exception("Timetable fetching error")
            }
        }
    }

    suspend fun insertTimeTable(classes: List<Predavanja>) {
        timeTableDao.insert(classes)
    }

    suspend fun fetchWeatherDetails(url : String): Current? {
        return when(val result = weatherNetworkService.fetchWeatherDetails(url)){
            is Result.WeatherResult.Success -> parseWeatherDetails(result.data)
            is Result.WeatherResult.Failure -> {
                Log.e(TAG, "Timetable fetching error")
                //throw Exception("Timetable fetching error")
                null
            }
        }
    }

    suspend fun fetchAttendance(user: User): Result.PrisutnostResult = attendanceService.fetchAttendance(user)
    
    suspend fun insertAttendance(attendance: List<Dolazak>) {
        attendanceDao.insert(attendance)
    }

    companion object {
        private val TAG = this.javaClass.canonicalName
    }

}
