package com.tstudioz.fax.fme.common.user

import com.tstudioz.fax.fme.common.user.models.UserRepositoryResult
import com.tstudioz.fax.fme.database.models.UserRealm

interface UserRepositoryInterface {

    suspend fun attemptLogin(username: String, password: String): UserRepositoryResult.LoginResult

    suspend fun getCurrentUserName(): String

    suspend fun getCurrentUser(): UserRealm?

    suspend fun deleteAllUserData()

}
