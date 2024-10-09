package com.tstudioz.fax.fme.networking.cookies

import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.CookieCache
import com.franmontiel.persistentcookiejar.persistence.CookiePersistor
import com.tstudioz.fax.fme.feature.iksica.services.IksicaService
import com.tstudioz.fax.fme.feature.login.services.UserService
import okhttp3.Cookie
import okhttp3.HttpUrl

class MonsterCookieJar(
    cache: CookieCache,
    persistor: CookiePersistor
) : PersistentCookieJar(cache, persistor) {

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val cookieToSave = cookies.map { cookie ->
            if (cookie.name != authCookieISSP) { return@map cookie }

            Cookie.Builder()
                .name(cookie.name)
                .value(cookie.value)
                .domain(cookie.domain)
                .path(cookie.path)
                .expiresAt(System.currentTimeMillis() + isspExpirationTime)
                .secure()
                .httpOnly()
                .build()
        }
        super.saveFromResponse(url, cookieToSave)
    }

    fun isFESBTokenValid(): Boolean {
        val cookies = loadForRequest(UserService.targetUrl)
        val authCookies = cookies
            .filter {
                it.name == authCookieFESB && it.expiresAt > System.currentTimeMillis()
            }

        return authCookies.isNotEmpty()
    }

    fun isISSPTokenValid(): Boolean {
        val cookies = loadForRequest(IksicaService.targetUrl)
        val authCookies = cookies
            .filter { it.name == authCookieISSP }

        return authCookies.isNotEmpty()
    }

    companion object {

        const val authCookieFESB = "Fesb.AuthCookie"
        const val authCookieISSP = ".AspNetCore.saml2"
        const val isspExpirationTime = 3600000L

    }

}