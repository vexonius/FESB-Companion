package com.tstudioz.fax.fme.feature.login.di

import com.tstudioz.fax.fme.feature.login.services.UserService
import com.tstudioz.fax.fme.feature.login.view.LoginViewModel
import com.tstudioz.fax.fme.feature.login.dao.UserDao
import com.tstudioz.fax.fme.feature.login.dao.UserDaoInterface
import com.tstudioz.fax.fme.common.user.UserRepository
import com.tstudioz.fax.fme.common.user.UserRepositoryInterface
import com.tstudioz.fax.fme.feature.login.services.UserServiceInterface
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@OptIn(InternalCoroutinesApi::class)
val loginModule = module {
    single<UserRepositoryInterface> { UserRepository(get(), get(), get()) }
    single<UserServiceInterface> { UserService(get()) }
    single<UserDaoInterface> { UserDao(get()) }
    viewModel { LoginViewModel(androidApplication(), get(), get()) }
}