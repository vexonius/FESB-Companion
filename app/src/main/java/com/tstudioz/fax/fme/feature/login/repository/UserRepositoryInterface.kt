package com.tstudioz.fax.fme.feature.login.repository

import com.tstudioz.fax.fme.feature.login.repository.models.UserRepositoryResult

interface UserRepositoryInterface {

    suspend fun attemptLogin(username: String, password: String): UserRepositoryResult.LoginResult

}
