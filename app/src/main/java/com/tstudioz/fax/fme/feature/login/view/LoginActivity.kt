package com.tstudioz.fax.fme.feature.login.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
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
        onBackListen()
        setContent {
            AppTheme {
                LoginCompose(
                    showLoading = loginViewModel.showLoading,
                    snackbarHostState = loginViewModel.snackbarHostState,
                    username = loginViewModel.username,
                    password = loginViewModel.password,
                    tryUserLogin = { loginViewModel.tryUserLogin() }
                )
            }
        }

        loginViewModel.checkIfFirstTimeInApp()
        loginViewModel.checkIfLoggedIn()
    }

    private fun onBackListen() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val a = Intent(Intent.ACTION_MAIN)
                a.addCategory(Intent.CATEGORY_HOME)
                a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(a)
            }
        })
    }

    private fun isUserLoggedIn() {
        loginViewModel.loggedIn.observe(this) { _ ->
            router.routeToHome()
        }
    }

}