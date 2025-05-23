package com.tstudioz.fax.fme.networking.session

import com.tstudioz.fax.fme.feature.login.dao.UserDao
import com.tstudioz.fax.fme.networking.cookies.MonsterCookieJar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class SessionDelegate(
    private val cookieJar: MonsterCookieJar,
    private val userDao: UserDao
) : SessionDelegateInterface {

    override val onUserDeleted: Flow<Boolean>
        get() = userDao.observeUserChanges().filter {
            it == null
        }.map {
            true
        }

    override val isSessionActive: Boolean
        get() = cookieJar.isFESBTokenValid()

    override fun clearSession() {
        cookieJar.clear()
    }

}