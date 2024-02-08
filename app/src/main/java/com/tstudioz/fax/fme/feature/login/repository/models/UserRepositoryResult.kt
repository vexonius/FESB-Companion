package com.tstudioz.fax.fme.feature.login.repository.models

import com.tstudioz.fax.fme.models.data.User

sealed class UserRepositoryResult {

    sealed class LoginResult : UserRepositoryResult() {
        data class Success(val data: User) : LoginResult()
        class Failure(throwable: Throwable) : LoginResult()
    }

}
