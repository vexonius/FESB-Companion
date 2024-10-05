package com.tstudioz.fax.fme.networking.cookies

import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.CookieCache
import com.franmontiel.persistentcookiejar.persistence.CookiePersistor
import com.tstudioz.fax.fme.feature.login.services.UserService

class MonsterCookieJar(
    cache: CookieCache,
    persistor: CookiePersistor
): PersistentCookieJar(cache, persistor) {

    fun isFESBTokenValid(): Boolean {
        val cookies = loadForRequest(UserService.targetUrl)
        val authCookies = cookies
            .filter {
                it.name == authCookieFESB && it.expiresAt > System.currentTimeMillis()
            }

        return authCookies.isNotEmpty()
    }

    companion object {

        const val authCookieFESB = "Fesb.AuthCookie"

    }

}