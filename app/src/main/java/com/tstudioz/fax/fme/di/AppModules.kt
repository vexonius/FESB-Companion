package com.tstudioz.fax.fme.di

import android.content.Context
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.tstudioz.fax.fme.data.Repository
import com.tstudioz.fax.fme.networking.NetworkService
import com.tstudioz.fax.fme.networking.PortalService
import com.tstudioz.fax.fme.ui.mainscreen.MainViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

@InternalCoroutinesApi
val module = module {
    single { Repository() }
    single { PortalService() }
    single { NetworkService() }
    single<OkHttpClient> { provideOkHttpClient(androidContext()) }
    viewModel { MainViewModel() }
}

fun provideOkHttpClient(context: Context) : OkHttpClient {
    return OkHttpClient.Builder()
            .callTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .cookieJar(PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context)))
            .build()
}
