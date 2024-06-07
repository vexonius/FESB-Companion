package com.tstudioz.fax.fme.models

import com.tstudioz.fax.fme.database.models.Dolazak
import com.tstudioz.fax.fme.database.models.Meni
import com.tstudioz.fax.fme.models.data.User

sealed class NetworkServiceResult {

    sealed class LoginResult : NetworkServiceResult() {
        data class Success(val data: User) : LoginResult()
        class Failure(throwable: Throwable) : LoginResult()
    }

    sealed class LogoutResult : NetworkServiceResult() {
        data class Success(val data: String) : LogoutResult()
        class Failure(throwable: Throwable) : LogoutResult()
    }

    sealed class TimeTableResult: NetworkServiceResult(){
        data class Success(val data: String) : TimeTableResult()
        class Failure(throwable: Throwable) : TimeTableResult()
    }

    sealed class PrisutnostResult: NetworkServiceResult(){
        class Success(val pris: MutableList<Dolazak>) : PrisutnostResult()
        class Failure(throwable: Throwable) : PrisutnostResult()
    }

    sealed class WeatherResult: NetworkServiceResult()  {
        data class Success(val data: String): WeatherResult()
        class Failure(exception: Throwable) : WeatherResult()
    }

    sealed class MenzaResult: NetworkServiceResult()  {
        data class Success(val data: String): MenzaResult()
        class Failure(exception: Throwable) : MenzaResult()
    }

}
