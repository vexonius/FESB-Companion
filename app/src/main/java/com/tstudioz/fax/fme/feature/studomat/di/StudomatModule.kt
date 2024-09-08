package com.tstudioz.fax.fme.feature.studomat.di

import com.tstudioz.fax.fme.feature.studomat.dao.StudomatDao
import com.tstudioz.fax.fme.feature.studomat.repository.StudomatRepository
import com.tstudioz.fax.fme.feature.studomat.services.StudomatService
import com.tstudioz.fax.fme.feature.studomat.view.StudomatViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val studomatModule = module {
    single{ StudomatService() }
    single { StudomatRepository(get(), get(), get(), get()) }
    single { StudomatDao(get()) }
    viewModel { StudomatViewModel(get(), androidContext(), get(), get()) }
}