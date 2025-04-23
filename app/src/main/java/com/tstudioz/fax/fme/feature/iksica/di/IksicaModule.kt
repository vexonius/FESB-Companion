package com.tstudioz.fax.fme.feature.iksica.di

import com.tstudioz.fax.fme.database.AppDatabase
import com.tstudioz.fax.fme.feature.iksica.dao.IksicaDao
import com.tstudioz.fax.fme.feature.iksica.repository.IksicaRepository
import com.tstudioz.fax.fme.feature.iksica.repository.IksicaRepositoryInterface
import com.tstudioz.fax.fme.feature.iksica.services.IksicaLoginService
import com.tstudioz.fax.fme.feature.iksica.services.IksicaLoginServiceInterface
import com.tstudioz.fax.fme.feature.iksica.services.IksicaService
import com.tstudioz.fax.fme.feature.iksica.services.IksicaServiceInterface
import com.tstudioz.fax.fme.feature.iksica.view.IksicaViewModel
import com.tstudioz.fax.fme.networking.cookies.MonsterCookieJar
import com.tstudioz.fax.fme.networking.interceptors.ISSPLoginInterceptor
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

@OptIn(InternalCoroutinesApi::class)
val iksicaModule = module {
    single<ISSPLoginInterceptor> { ISSPLoginInterceptor(get(), get(), get()) }
    single<IksicaLoginServiceInterface> { IksicaLoginService(get(), null, "", "") }
    single<OkHttpClient>(named("ISSPPortalClient")) { provideISSPPortalClient(get(), get()) }
    single<IksicaServiceInterface> { IksicaService(get(named("ISSPPortalClient"))) }
    single<IksicaRepositoryInterface> { IksicaRepository(get(), get()) }
    single<IksicaDao> { getIksicaDao(get()) }
    viewModel { IksicaViewModel(get(), get()) }
}

fun provideISSPPortalClient(
    monsterCookieJar: MonsterCookieJar,
    interceptor: ISSPLoginInterceptor,
): OkHttpClient {
    return OkHttpClient.Builder()
        .callTimeout(15, TimeUnit.SECONDS)
        .connectTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(interceptor)
        .cookieJar(monsterCookieJar)
        .build()
}


fun getIksicaDao(db: AppDatabase): IksicaDao {
    return db.iksicaDao()
}