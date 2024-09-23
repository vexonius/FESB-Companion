package com.tstudioz.fax.fme.feature.studomat.di

import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.tstudioz.fax.fme.feature.studomat.dao.StudomatDao
import com.tstudioz.fax.fme.feature.studomat.repository.StudomatRepository
import com.tstudioz.fax.fme.feature.studomat.services.StudomatService
import com.tstudioz.fax.fme.feature.studomat.view.StudomatViewModel
import okhttp3.Cookie
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val studomatModule = module {
    single { StudomatService(get(named("clientStudomat"))) }
    single { StudomatRepository(get(), get(), get()) }
    single { StudomatDao(get()) }
    single(named("clientStudomat")) { clientStudomat }
    viewModel { StudomatViewModel(get(), get(), get()) }
}

private val clientStudomat: OkHttpClient = OkHttpClient.Builder().cookieJar(object : ClearableCookieJar {
    private val cookieStore = HashMap<String, MutableList<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (cookieStore[url.host] == null)
            cookieStore[url.host] = mutableListOf()
        cookies.forEach { cookie ->
            cookieStore[url.host]?.find { it.name == cookie.name }
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
}).build()