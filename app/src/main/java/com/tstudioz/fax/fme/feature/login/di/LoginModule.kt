package com.tstudioz.fax.fme.feature.login.di

import com.tstudioz.fax.fme.feature.login.services.UserService
import com.tstudioz.fax.fme.feature.login.view.LoginViewModel
import com.tstudioz.fax.fme.models.data.UserDao
import com.tstudioz.fax.fme.models.data.UserDaoInterface
import com.tstudioz.fax.fme.feature.login.repository.UserRepository
import com.tstudioz.fax.fme.feature.login.repository.UserRepositoryInterface
import com.tstudioz.fax.fme.feature.login.services.UserServiceInterface
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

@OptIn(InternalCoroutinesApi::class)
val loginModule = module {

    single<UserRepositoryInterface> { UserRepository(get(), get(), get(), get()) }
    single<UserServiceInterface> { UserService(get()) }
    single<UserDaoInterface> { UserDao(get()) }
    viewModel { LoginViewModel(androidApplication(), get()) }

}