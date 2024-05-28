package com.tstudioz.fax.fme.feature.studomat.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.tstudioz.fax.fme.feature.studomat.repository.StudomatRepository
import com.tstudioz.fax.fme.feature.studomat.services.StudomatService
import com.tstudioz.fax.fme.feature.studomat.view.StudomatViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val studomatModule = module {
    single{ StudomatService() }
    single { StudomatRepository(get()) }
    viewModel { StudomatViewModel(get(), androidContext(), get()) }
}