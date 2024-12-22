package com.tstudioz.fax.fme.feature.login.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.feature.login.compose.LoginCompose
import com.tstudioz.fax.fme.routing.LoginRouter
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

@OptIn(InternalCoroutinesApi::class)
class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModel()
    private val router: LoginRouter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        router.register(this)

        isUserLoggedIn()
        setContent() {
            AppTheme { LoginCompose(loginViewModel) }
        }

        loginViewModel.checkIfFirstTimeInApp()
        loginViewModel.checkIfLoggedIn()
    }

    private fun isUserLoggedIn() {
        loginViewModel.loggedIn.observe(this) { _ ->
            router.routeToHome()
        }
    }

}