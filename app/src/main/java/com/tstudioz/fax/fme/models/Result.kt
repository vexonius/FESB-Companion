package com.tstudioz.fax.fme.models


sealed class Result {

    sealed class LoginResult : Result() {
        data class Success(val data: User) : LoginResult()
        class Failure(val throwable: Throwable) : LoginResult()
    }

    sealed class LogoutResult : Result() {
        data class Success(val data: String) : LogoutResult()
        class Failure(val throwable: Throwable) : LogoutResult()
    }

    sealed class TimeTableResult: Result(){
        data class Success(val data: String) : TimeTableResult()
        class Failure(val throwable: Throwable) : TimeTableResult()
    }

}