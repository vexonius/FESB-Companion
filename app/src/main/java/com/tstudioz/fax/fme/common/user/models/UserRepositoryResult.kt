package com.tstudioz.fax.fme.common.user.models

sealed class UserRepositoryResult {

    sealed class LoginResult : UserRepositoryResult() {
        data class Success(val data: User) : LoginResult()
        class Failure(throwable: Throwable) : LoginResult()
    }

}
