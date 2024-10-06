package com.tstudioz.fax.fme.feature.iksica.di

import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.tstudioz.fax.fme.feature.iksica.IksicaViewModel
import com.tstudioz.fax.fme.feature.iksica.dao.IksicaDao
import com.tstudioz.fax.fme.feature.iksica.dao.IksicaDaoInterface
import com.tstudioz.fax.fme.feature.iksica.repository.IksicaRepository
import com.tstudioz.fax.fme.feature.iksica.repository.IksicaRepositoryInterface
import com.tstudioz.fax.fme.feature.iksica.services.IksicaService
import com.tstudioz.fax.fme.feature.iksica.services.IksicaServiceInterface
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.Cookie
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

@OptIn(InternalCoroutinesApi::class)
val iksicaModule = module {
    single<IksicaRepositoryInterface> { IksicaRepository(get(), get()) }
    single<IksicaServiceInterface> { IksicaService(get(named("clientIksica"))) }
    single<IksicaDaoInterface> { IksicaDao(get()) }
    single(named("clientIksica")) { clientIksica }
    viewModel { IksicaViewModel( get(),get()) }
}

private val clientIksica: OkHttpClient = OkHttpClient.Builder().cookieJar(object : ClearableCookieJar {
    private val cookieStore = HashMap<String, MutableList<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (cookieStore[url.host] == null)
            cookieStore[url.host] = mutableListOf()
        cookies.forEach { cookie ->
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
}).build()
