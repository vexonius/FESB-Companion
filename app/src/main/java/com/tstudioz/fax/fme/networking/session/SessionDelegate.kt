package com.tstudioz.fax.fme.networking.session

import com.tstudioz.fax.fme.feature.login.dao.UserDaoInterface
import com.tstudioz.fax.fme.networking.cookies.MonsterCookieJar
import kotlinx.coroutines.flow.Flow

class SessionDelegate(
    private val cookieJar: MonsterCookieJar,
    private val userDao: UserDaoInterface
): SessionDelegateInterface {

    override val onUserDeleted: Flow<Unit>
        get() = userDao.observeUserChanges()

    override val isSessionActive: Boolean
        get() = cookieJar.isFESBTokenValid()

    override fun clearSession() {
        cookieJar.clear()
    }

}