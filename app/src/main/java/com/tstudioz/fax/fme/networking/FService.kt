package com.tstudioz.fax.fme.networking

import com.tstudioz.fax.fme.models.Result
import kotlinx.coroutines.flow.Flow


interface FService {

    suspend fun loginUser(): Flow<Result.LoginResult>
    fun logoutUser(): Flow<Result.LogoutResult>
}

