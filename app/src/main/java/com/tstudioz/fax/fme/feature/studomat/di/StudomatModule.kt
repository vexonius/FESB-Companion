package com.tstudioz.fax.fme.feature.studomat.di

import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.tstudioz.fax.fme.feature.studomat.dao.StudomatDao
import com.tstudioz.fax.fme.feature.studomat.repository.StudomatRepository
import com.tstudioz.fax.fme.feature.studomat.services.StudomatLoginService
import com.tstudioz.fax.fme.feature.studomat.services.StudomatService
import com.tstudioz.fax.fme.feature.studomat.view.StudomatViewModel
import com.tstudioz.fax.fme.networking.cookies.MonsterCookieJar
import com.tstudioz.fax.fme.networking.interceptors.ISVULoginInterceptor
import okhttp3.Cookie
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val studomatModule = module {
    single<ISVULoginInterceptor> { ISVULoginInterceptor(get(), get(), get()) }
    single<StudomatLoginService>{ StudomatLoginService(get()) }
    single(named("clientStudomat")) { provideISVUPortalClient(get(), get()) }
    single { StudomatService(get(named("clientStudomat"))) }
    single { StudomatRepository(get(), get(), get()) }
    single { StudomatDao(get()) }
    viewModel { StudomatViewModel(get(), get(), get(), get()) }
}
fun provideISVUPortalClient(
    monsterCookieJar: MonsterCookieJar,
    interceptor: ISVULoginInterceptor,
) : OkHttpClient {
    return OkHttpClient.Builder()
        .callTimeout(15, TimeUnit.SECONDS)
        .connectTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(interceptor)
        .cookieJar(monsterCookieJar)
        .build()
}

val cookieJar = cok1()
val cl = OkHttpClient.Builder()
    .callTimeout(15, TimeUnit.SECONDS)
    .connectTimeout(15, TimeUnit.SECONDS)
    .cookieJar(cookieJar)
    .build()

    class cok1 : ClearableCookieJar {
    private val cookieStore = HashMap<String, MutableList<Cookie>>()

    val authCookieISVU = "JSESSIONID"
    val expirationTime = 3600000L
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val cookieToSave = cookies.map { cookie ->
            if (cookie.name != authCookieISVU) {
                return@map cookie
            }

            Cookie.Builder()
                .name(cookie.name)
                .value(cookie.value)
                .domain(cookie.domain)
                .path(cookie.path)
                .expiresAt(System.currentTimeMillis() + MonsterCookieJar.expirationTime)
                .secure()
                .httpOnly()
                .build()
        }
        if (cookieStore[url.host] == null)
            cookieStore[url.host] = mutableListOf()
        cookieToSave.forEach { cookie ->
            cookieStore[url.host]
                ?.find { it.name == cookie.name }
                ?.let { cookieStore[url.host]?.remove(it) }
            cookieStore[url.host]?.add(cookie)
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore[url.host] ?: ArrayList()
    }

    override fun clearSession() {
        cookieStore.clear()
    }

    override fun clear() {
        cookieStore.clear()
    }
    fun isISVUTokenValid(): Boolean {
        val cookies = loadForRequest(StudomatService.targetUrl)
        val authCookies = cookies.filter { it.name == authCookieISVU }

        return authCookies.isNotEmpty()
    }

}