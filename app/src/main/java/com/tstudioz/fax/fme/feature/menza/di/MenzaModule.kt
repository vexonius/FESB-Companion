package com.tstudioz.fax.fme.feature.menza.di

import com.tstudioz.fax.fme.feature.login.services.UserService
import com.tstudioz.fax.fme.feature.login.view.LoginViewModel
import com.tstudioz.fax.fme.models.data.UserDao
import com.tstudioz.fax.fme.models.data.UserDaoInterface
import com.tstudioz.fax.fme.feature.login.repository.UserRepository
import com.tstudioz.fax.fme.feature.login.repository.UserRepositoryInterface
import com.tstudioz.fax.fme.feature.login.services.UserServiceInterface
import com.tstudioz.fax.fme.feature.menza.dao.MenzaDao
import com.tstudioz.fax.fme.feature.menza.dao.interfaces.MenzaDaoInterface
import com.tstudioz.fax.fme.feature.menza.service.MenzaService
import com.tstudioz.fax.fme.feature.menza.service.MenzaServiceInterface
import com.tstudioz.fax.fme.feature.menza.view.MenzaViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@OptIn(InternalCoroutinesApi::class)
val menzaModule = module {
    single<MenzaDaoInterface> { MenzaDao(get()) }
    single<MenzaServiceInterface> { MenzaService(get()) }
    viewModel { MenzaViewModel(androidApplication(), get()) }
}