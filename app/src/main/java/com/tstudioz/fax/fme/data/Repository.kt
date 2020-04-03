package com.tstudioz.fax.fme.data

import android.util.Log
import com.tstudioz.fax.fme.models.PortalResult
import com.tstudioz.fax.fme.models.User
import com.tstudioz.fax.fme.networking.PortalService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.koin.java.KoinJavaComponent.inject


class Repository {

    private val service: PortalService by inject(PortalService::class.java)

    init {

    }

    suspend fun attemptLogin(): Flow<User> = flow {
        service.loginUser().collect { result ->
            when (result) {
                is PortalResult.LoginResult.Success -> emit(result.data)
                is PortalResult.LoginResult.Failure -> Log.d(TAG, "Doslo je do pogreske")
            }
        }

    }

    companion object {
        private val TAG = this.javaClass.canonicalName
    }


}