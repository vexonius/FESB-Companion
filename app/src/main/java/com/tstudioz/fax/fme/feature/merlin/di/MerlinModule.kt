package com.tstudioz.fax.fme.feature.merlin.di

import com.tstudioz.fax.fme.feature.login.services.UserService
import com.tstudioz.fax.fme.models.data.UserDao
import com.tstudioz.fax.fme.models.data.UserDaoInterface
import com.tstudioz.fax.fme.feature.login.repository.UserRepository
import com.tstudioz.fax.fme.feature.login.repository.UserRepositoryInterface
import com.tstudioz.fax.fme.feature.login.services.UserServiceInterface
import com.tstudioz.fax.fme.feature.merlin.repository.MerlinRepository
import com.tstudioz.fax.fme.feature.merlin.repository.MerlinRepositoryInterface
import com.tstudioz.fax.fme.feature.merlin.repository.models.data.MerlinDao
import com.tstudioz.fax.fme.feature.merlin.repository.models.data.MerlinDaoInterface
import com.tstudioz.fax.fme.feature.merlin.services.MerlinService
import com.tstudioz.fax.fme.feature.merlin.services.MerlinServiceInterface
import com.tstudioz.fax.fme.feature.merlin.view.MerlinViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

@OptIn(InternalCoroutinesApi::class)
val merlinModule = module {
    single<MerlinRepositoryInterface> { MerlinRepository(get(), get(), get(), get()) }
    single<MerlinServiceInterface> { MerlinService(get(named("merlinClient"))) }
    single<MerlinDaoInterface> { MerlinDao(get()) }
    single(named("merlinClient")) { client }
    viewModel { MerlinViewModel(androidApplication(), get(), get()) }
}

class MyCookieJarmerlin : CookieJar {
    val cookieStore = HashMap<String, MutableList<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (cookieStore[url.host] == null)
            cookieStore[url.host] = mutableListOf()
        cookies.forEach { cookie ->
            cookieStore[url.host]?.find { it.name == cookie.name }?.let {
                cookieStore[url.host]?.remove(it)
            }
            cookieStore[url.host]?.add(cookie)
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore[url.host] ?: ArrayList()
    }
}

private val client = OkHttpClient.Builder().cookieJar(MyCookieJarmerlin()).build()