package com.tstudioz.fax.fme.models.interfaces

import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.models.data.User
import kotlinx.coroutines.flow.Flow

interface UserServiceInterface {

    suspend fun loginUser(username: String, password: String): NetworkServiceResult.LoginResult
    fun logoutUser(): Flow<NetworkServiceResult.LogoutResult>

}

