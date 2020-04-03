package com.tstudioz.fax.fme.networking

import com.tstudioz.fax.fme.models.PortalResult
import kotlinx.coroutines.flow.Flow


interface FService {

    suspend fun loginUser(): Flow<PortalResult.LoginResult>
    fun logoutUser(): Flow<PortalResult.LogoutResult>
}

