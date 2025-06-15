package com.tstudioz.fax.fme.common.user

import com.tstudioz.fax.fme.common.user.models.User
import com.tstudioz.fax.fme.common.user.models.UserRepositoryResult

interface UserRepositoryInterface {

    suspend fun attemptLogin(username: String, password: String): UserRepositoryResult.LoginResult

    suspend fun getCurrentUserName(): String

    suspend fun getCurrentUser(): User

    suspend fun deleteAllUserData()

}
