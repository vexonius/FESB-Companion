package com.tstudioz.fax.fme.networking.cookies

import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.CookieCache
import com.franmontiel.persistentcookiejar.persistence.CookiePersistor
import com.tstudioz.fax.fme.feature.iksica.services.IksicaService
import com.tstudioz.fax.fme.feature.login.services.UserService
import com.tstudioz.fax.fme.feature.studomat.services.StudomatService
import okhttp3.Cookie
import okhttp3.HttpUrl

class MonsterCookieJar(
    private val cache: CookieCache,
    private val persistor: CookiePersistor
) : PersistentCookieJar(cache, persistor) {

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val cookiesToSave = cookies.map { cookie ->
            if (!(cookie.name == authCookieISSP || cookie.name == authCookieISVU)) {
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
        super.saveFromResponse(url, cookiesToSave)
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        super.loadForRequest(url)
        val cookies = cache.filter {
            url.host.endsWith(it.domain)
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

    fun clearISVUCookie() {
        val cookie = persistor.loadAll().filter{
            it.name == authCookieISVU
        }
        persistor.removeAll(cookie)
        cache.removeAll { cookie.contains(it) }
    }

    companion object {

        const val authCookieFESB = "Fesb.AuthCookie"
        const val authCookieISSP = ".AspNetCore.saml2"
        const val authCookieISVU = "JSESSIONID"
        const val expirationTime = 3600000L

    }

}