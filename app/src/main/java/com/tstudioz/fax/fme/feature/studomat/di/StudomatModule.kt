package com.tstudioz.fax.fme.feature.studomat.di

import com.tstudioz.fax.fme.database.AppDatabase
import com.tstudioz.fax.fme.feature.studomat.dao.StudomatDao
import com.tstudioz.fax.fme.feature.studomat.repository.StudomatRepository
import com.tstudioz.fax.fme.feature.studomat.services.StudomatLoginService
import com.tstudioz.fax.fme.feature.studomat.services.StudomatLoginServiceInterface
import com.tstudioz.fax.fme.feature.studomat.services.StudomatService
import com.tstudioz.fax.fme.feature.studomat.view.StudomatViewModel
import com.tstudioz.fax.fme.networking.cookies.MonsterCookieJar
import com.tstudioz.fax.fme.networking.interceptors.ISVULoginInterceptor
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val studomatModule = module {
    single<ISVULoginInterceptor> { ISVULoginInterceptor(get(), get(), get()) }
    single<StudomatLoginServiceInterface>{ StudomatLoginService(get()) }
    single<OkHttpClient>(named("clientStudomat")) { provideISVUPortalClient(get(), get()) }
    single { StudomatService(get(named("clientStudomat"))) }
    single { StudomatRepository(get(), get(), get()) }
    single { getStudomatDao(get()) }
    viewModel { StudomatViewModel(get(), get(), get()) }
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

fun getStudomatDao(db: AppDatabase): StudomatDao {
    return db.studomatDao()
}