package com.tstudioz.fax.fme.feature.iksica.di

import android.content.Context
import com.tstudioz.fax.fme.feature.iksica.IksicaViewModel
import com.tstudioz.fax.fme.feature.iksica.dao.IksicaDao
import com.tstudioz.fax.fme.feature.iksica.dao.IksicaDaoInterface
import com.tstudioz.fax.fme.feature.iksica.repository.IksicaRepository
import com.tstudioz.fax.fme.feature.iksica.repository.IksicaRepositoryInterface
import com.tstudioz.fax.fme.feature.iksica.services.IksicaService
import com.tstudioz.fax.fme.feature.iksica.services.IksicaServiceInterface
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

@OptIn(InternalCoroutinesApi::class)
val iksicaModule = module {
    single<IksicaRepositoryInterface> { IksicaRepository(get(), get(), get()) }
    single<IksicaServiceInterface> { IksicaService(get())}
    single<IksicaDaoInterface> { IksicaDao(get()) }
    viewModel { IksicaViewModel(androidApplication(), get()) }
}

