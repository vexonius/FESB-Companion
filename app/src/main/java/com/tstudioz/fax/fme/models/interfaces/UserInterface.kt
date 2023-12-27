package com.tstudioz.fax.fme.models.interfaces

import com.tstudioz.fax.fme.models.Result
import com.tstudioz.fax.fme.models.data.User
import kotlinx.coroutines.flow.Flow


interface UserInterface {


    suspend fun loginUser(user: User): Result.LoginResult
    fun logoutUser(): Flow<Result.LogoutResult>
}

