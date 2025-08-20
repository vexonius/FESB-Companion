package com.tstudioz.fax.fme.feature.login.services

import com.tstudioz.fax.fme.common.user.models.User
import com.tstudioz.fax.fme.models.NetworkServiceResult

interface UserServiceInterface {

    suspend fun loginUser(username: String, password: String): NetworkServiceResult.LoginResult

}

