package com.tstudioz.fax.fme.feature.login.dao

import com.tstudioz.fax.fme.database.models.UserRealm

interface UserDaoInterface {

    suspend fun insert(user: UserRealm)

    suspend fun getUser(): UserRealm

}
