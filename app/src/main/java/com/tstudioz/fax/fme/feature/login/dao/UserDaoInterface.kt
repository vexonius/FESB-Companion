package com.tstudioz.fax.fme.feature.login.dao

import com.tstudioz.fax.fme.database.models.UserRealm
import kotlinx.coroutines.flow.Flow

interface UserDaoInterface {

    fun observeUserChanges(): Flow<Unit>

    suspend fun insert(user: UserRealm)

    suspend fun getUser(): UserRealm

    suspend fun deleteAllUserData()

}
