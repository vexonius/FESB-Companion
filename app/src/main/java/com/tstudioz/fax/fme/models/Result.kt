package com.tstudioz.fax.fme.models

import com.tstudioz.fax.fme.database.models.Dolazak
import com.tstudioz.fax.fme.models.data.User


sealed class Result {

    sealed class LoginResult : Result() {
        data class Success(val data: User) : LoginResult()
        class Failure(throwable: Throwable) : LoginResult()
    }

    sealed class LogoutResult : Result() {
        data class Success(val data: String) : LogoutResult()
        class Failure(throwable: Throwable) : LogoutResult()
    }

    sealed class TimeTableResult: Result(){
        data class Success(val data: String) : TimeTableResult()
        class Failure(throwable: Throwable) : TimeTableResult()
    }

    sealed class PrisutnostResult: Result(){
        class Success(val pris: MutableList<Dolazak>) : PrisutnostResult()
        class Failure(throwable: Throwable) : PrisutnostResult()
    }


    sealed class WeatherResult: Result()  {
        data class Success(val data: String): WeatherResult()
        class Failure(exception: Throwable) : WeatherResult()
    }
}