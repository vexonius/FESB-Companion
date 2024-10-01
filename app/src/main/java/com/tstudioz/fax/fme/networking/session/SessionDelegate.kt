package com.tstudioz.fax.fme.networking.session

import com.tstudioz.fax.fme.networking.cookies.MonsterCookieJar

class SessionDelegate(private val cookieJar: MonsterCookieJar): SessionDelegateInterface {

    override val isSessionActive: Boolean
        get() = cookieJar.isFESBTokenValid()

}