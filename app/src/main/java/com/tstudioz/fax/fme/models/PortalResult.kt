package com.tstudioz.fax.fme.models


sealed class PortalResult {

    sealed class LoginResult : PortalResult() {
        data class Success(val data: User) : LoginResult()
        class Failure(throwable: Throwable) : LoginResult()
    }

    sealed class LogoutResult : PortalResult() {
        data class Success(val data: String) : LogoutResult()
        class Failure(throwable: Throwable) : LogoutResult()
    }

}