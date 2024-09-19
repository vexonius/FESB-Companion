package com.tstudioz.fax.fme.models

import com.tstudioz.fax.fme.database.models.AttendanceEntry
import com.tstudioz.fax.fme.models.data.User

sealed class NetworkServiceResult {

    sealed class LoginResult : NetworkServiceResult() {
        data class Success(val data: User) : LoginResult()
        class Failure(val throwable: Throwable) : LoginResult()
    }

    sealed class LogoutResult : NetworkServiceResult() {
        data class Success(val data: String) : LogoutResult()
        class Failure(val throwable: Throwable) : LogoutResult()
    }

    sealed class TimeTableResult: NetworkServiceResult(){
        data class Success(val data: String) : TimeTableResult()
        class Failure(val throwable: Throwable) : TimeTableResult()
    }

    sealed class IksicaResult: NetworkServiceResult(){
        data class Success(val data: String) : IksicaResult()
        class Failure(val throwable: Throwable) : IksicaResult()
    }

    sealed class AttendanceFetchResult: NetworkServiceResult(){
        class Success(val data: String) : AttendanceFetchResult()
        class Failure(throwable: Throwable) : AttendanceFetchResult()
    }

    sealed class AttendanceParseResult: NetworkServiceResult(){
        class Success(val data: List<List<AttendanceEntry>>) : AttendanceParseResult()
        class Failure(throwable: Throwable) : AttendanceParseResult()
    }

    sealed class WeatherResult: NetworkServiceResult()  {
        data class Success(val data: String): WeatherResult()
        class Failure(val exception: Throwable) : WeatherResult()
    }

}
