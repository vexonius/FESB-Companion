package com.tstudioz.fax.fme.networking.cookies

import android.util.Log
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.CookieCache
import com.franmontiel.persistentcookiejar.persistence.CookiePersistor
import com.tstudioz.fax.fme.feature.iksica.services.IksicaService
import com.tstudioz.fax.fme.feature.login.services.UserService
import com.tstudioz.fax.fme.feature.studomat.services.StudomatService
import okhttp3.Cookie
import okhttp3.HttpUrl

class MonsterCookieJar(
    val cache: CookieCache,
    persistor: CookiePersistor
) : PersistentCookieJar(cache, persistor) {

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cache.removeAll { it.name == authCookieISVU }
        val cookieToSave = cookies.map { cookie ->
            if (!(cookie.name == authCookieISSP || cookie.name == authCookieISVU || cookie.name == "ISVU")) {
                return@map cookie
            }

            Cookie.Builder()
                .name(cookie.name)
                .value(cookie.value)
                .domain(cookie.domain)
                .path(cookie.path)
                .expiresAt(System.currentTimeMillis() + expirationTime)
                .secure()
                .httpOnly()
                .build()
        }
        cookies.forEach {
            if (it.name == "ISVU")
                Log.d("MonsterCookieJar", "ISVU cookie saved!")
        }
        super.saveFromResponse(url, cookieToSave)
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookies = cache.filter {
            it.domain == url.host
        }
        return cookies
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
        val authCookies = cookies.filter { it.name == authCookieISSP }

        return authCookies.isNotEmpty()
    }

    fun isISVUTokenValid(): Boolean {
        val cookies = loadForRequest(StudomatService.targetUrl)
        val authCookies = cookies.filter { it.name == authCookieISVU }

        return authCookies.isNotEmpty()
    }

    companion object {

        const val authCookieFESB = "Fesb.AuthCookie"
        const val authCookieISSP = ".AspNetCore.saml2"
        const val authCookieISVU = "JSESSIONID"
        const val expirationTime = 3600000L

    }

}