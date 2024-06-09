package com.tstudioz.fax.fme.models.data

import android.util.Log
import com.tstudioz.fax.fme.database.models.Dolazak
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.feature.attendance.dao.AttendanceDaoInterface
import com.tstudioz.fax.fme.feature.login.services.UserServiceInterface
import com.tstudioz.fax.fme.feature.timetable.dao.interfaces.TimeTableDaoInterface
import com.tstudioz.fax.fme.feature.timetable.parseTimetable
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.feature.attendance.services.AttendanceServiceInterface
import com.tstudioz.fax.fme.feature.timetable.services.interfaces.TimetableServiceInterface
import com.tstudioz.fax.fme.models.interfaces.WeatherNetworkInterface
import com.tstudioz.fax.fme.models.util.parseWeatherDetails
import com.tstudioz.fax.fme.weather.Current
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class UserRepository(
    private val service: UserServiceInterface,
    private val timetableService: TimetableServiceInterface,
    private val weatherNetworkService: WeatherNetworkInterface,
    private val attendanceService: AttendanceServiceInterface,
    private val timeTableDao: TimeTableDaoInterface,
    private val attendanceDao: AttendanceDaoInterface
) : UserRepositoryInterface {

    override suspend fun attemptLogin(user: User): User {
        return when (val result = service.loginUser(user.username, user.password)) {
            is NetworkServiceResult.LoginResult.Success -> (result.data)
            is NetworkServiceResult.LoginResult.Failure -> {
                Log.d(TAG, "Doslo je do pogreske")
                (User("",""))
            }
        }
    }

    override suspend fun fetchTimetable(user: String, startDate: LocalDate, endDate: LocalDate): List<Event> {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
        val requestUrl = "https://raspored.fesb.unist.hr/part/raspored/kalendar?" +
                "DataType=User&DataId=$user" +
                "&MinDate=${dateFormatter.format(startDate)}" +
                "&MaxDate=${dateFormatter.format(endDate)}"
        return when(val result = timetableService.fetchTimeTable(requestUrl)){
            is NetworkServiceResult.TimeTableResult.Success -> parseTimetable(result.data)
            is NetworkServiceResult.TimeTableResult.Failure -> {
                Log.e(TAG, "Timetable fetching error")
                throw Exception("Timetable fetching error")
            }
        }
    }

    override suspend fun insertTimeTable(classes: List<Event>) {
        timeTableDao.insert(classes)
    }

    override suspend fun fetchWeatherDetails(url : String): Current? {
        return when(val result = weatherNetworkService.fetchWeatherDetails(url)){
            is NetworkServiceResult.WeatherResult.Success -> parseWeatherDetails(result.data)
            is NetworkServiceResult.WeatherResult.Failure -> {
                Log.e(TAG, "Timetable fetching error")
                //throw Exception("Timetable fetching error")
                null
            }
        }
    }

    override suspend fun fetchAttendance(user: User): NetworkServiceResult.AttendanceFetchResult = attendanceService.fetchAttendance(user)

    override suspend fun insertAttendance(attendance: List<Dolazak>) {
        attendanceDao.insert(attendance)
    }

    companion object {
        private val TAG = this.javaClass.canonicalName
    }

}
