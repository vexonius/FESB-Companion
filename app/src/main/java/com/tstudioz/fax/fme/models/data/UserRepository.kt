package com.tstudioz.fax.fme.models.data

import android.util.Log
import com.tstudioz.fax.fme.database.models.Dolazak
import com.tstudioz.fax.fme.database.models.Predavanja
import com.tstudioz.fax.fme.models.Result
import com.tstudioz.fax.fme.models.interfaces.AttendanceServiceInterface
import com.tstudioz.fax.fme.models.interfaces.TimetableServiceInterface
import com.tstudioz.fax.fme.models.interfaces.UserServiceInterface
import com.tstudioz.fax.fme.models.interfaces.WeatherNetworkInterface
import com.tstudioz.fax.fme.models.util.parseTimetable
import com.tstudioz.fax.fme.models.util.parseWeatherDetails
import com.tstudioz.fax.fme.weather.Current

class UserRepository(
    private val service: UserServiceInterface,
    private val timetableService: TimetableServiceInterface,
    private val weatherNetworkService: WeatherNetworkInterface,
    private val attendanceService: AttendanceServiceInterface,
    private val timeTableDao: TimeTableDaoInterface,
    private val attendanceDao: AttendanceDaoInterface) : UserRepositoryInterface {

    override suspend fun attemptLogin(user: User): User {
        return when (val result = service.loginUser(user)) {
            is Result.LoginResult.Success -> (result.data)
            is Result.LoginResult.Failure -> {
                Log.d(TAG, "Doslo je do pogreske")
                (User("","",""))
            }
        }
    }

    override suspend fun fetchTimetable(user: String, startDate: String, endDate: String): List<TimetableItem> {
        return when(val result = timetableService.fetchTimeTable(user, startDate, endDate)){
            is Result.TimeTableResult.Success -> parseTimetable(result.data)
            is Result.TimeTableResult.Failure -> {
                Log.e(TAG, "Timetable fetching error")
                throw Exception("Timetable fetching error")
            }
        }
    }

    override suspend fun insertTimeTable(classes: List<Predavanja>) {
        timeTableDao.insert(classes)
    }

    override suspend fun fetchWeatherDetails(url : String): Current? {
        return when(val result = weatherNetworkService.fetchWeatherDetails(url)){
            is Result.WeatherResult.Success -> parseWeatherDetails(result.data)
            is Result.WeatherResult.Failure -> {
                Log.e(TAG, "Timetable fetching error")
                //throw Exception("Timetable fetching error")
                null
            }
        }
    }

    override suspend fun fetchAttendance(user: User): Result.PrisutnostResult = attendanceService.fetchAttendance(user)

    override suspend fun insertAttendance(attendance: List<Dolazak>) {
        attendanceDao.insert(attendance)
    }

    companion object {
        private val TAG = this.javaClass.canonicalName
    }

}
