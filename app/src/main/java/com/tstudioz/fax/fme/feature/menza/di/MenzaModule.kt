package com.tstudioz.fax.fme.feature.menza.di

import com.tstudioz.fax.fme.feature.menza.repository.CamerasRepository
import com.tstudioz.fax.fme.feature.menza.repository.CamerasRepositoryInterface
import com.tstudioz.fax.fme.feature.menza.service.CamerasService
import com.tstudioz.fax.fme.feature.menza.service.CamerasServiceInterface
import com.tstudioz.fax.fme.feature.menza.repository.MenzaRepository
import com.tstudioz.fax.fme.feature.menza.repository.MenzaRepositoryInterface
import com.tstudioz.fax.fme.feature.menza.service.MenzaService
import com.tstudioz.fax.fme.feature.menza.service.MenzaServiceInterface
import com.tstudioz.fax.fme.feature.menza.view.MenzaViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

@OptIn(InternalCoroutinesApi::class)
val menzaModule = module {
    single<MenzaServiceInterface> { MenzaService(get()) }
    single<CamerasServiceInterface> { CamerasService(get()) }
    single<MenzaRepositoryInterface> { MenzaRepository(get()) }
    single<CamerasRepositoryInterface> { CamerasRepository(get()) }
    viewModel { MenzaViewModel(androidApplication(), get(), get()) }
}