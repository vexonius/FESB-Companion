package com.tstudioz.fax.fme.feature.login.view

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.snackbar.Snackbar
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.databinding.ActivityLoginBinding
import com.tstudioz.fax.fme.view.activities.MainActivity
import com.tstudioz.fax.fme.view.activities.Welcome
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel

@OptIn(InternalCoroutinesApi::class)
class LoginActivity : AppCompatActivity() {

    private var snack: Snackbar? = null
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        firstTimeWelcomeShowcase()
        isUserLoggedIn()
        observeInputFields()
        loadBlueButton()
        onBackListen()
        observeErrorMessages()

        loginViewModel.checkIfFirstTimeInApp()
        loginViewModel.checkIfLoggedIn()
    }

    private fun onBackListen(){
        onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val a = Intent(Intent.ACTION_MAIN)
                a.addCategory(Intent.CATEGORY_HOME)
                a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(a)
            }
        })
    }

    private fun observeErrorMessages() {
        loginViewModel.errorMessage.observe(this) { message ->
            message?.let {
                showErrorSnack(message)
                binding.progressLogin.visibility = View.INVISIBLE
                binding.loginButton.visibility = View.VISIBLE
            }
        }
    }

    private fun firstTimeWelcomeShowcase() {
        loginViewModel.firstTimeInApp.observe(this) { firstTime ->
            if (firstTime) {
                startActivity(Intent(this@LoginActivity, Welcome::class.java))
            }
        }
    }

    private fun isUserLoggedIn() {
        loginViewModel.loggedIn.observe(this) { loggedIn ->
            if (loggedIn) {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            }
        }
    }

    private fun observeInputFields() {
        binding.loginInput.doOnTextChanged { text, start, before, count ->
            loginViewModel.username.value = text.toString()
        }

        binding.loginPass.doOnTextChanged { text, start, before, count ->
            loginViewModel.password.value = text.toString()
        }
    }

    private fun loadBlueButton() {
        binding.loginButton.setOnClickListener {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)

            binding.progressLogin.visibility = View.VISIBLE
            binding.loginButton.visibility = View.INVISIBLE

            loginViewModel.tryUserLogin()
        }
    }

    private fun showErrorSnack(message: String) {
        binding.progressLogin.visibility = View.VISIBLE
        binding.loginButton.visibility = View.INVISIBLE

        snack = message?.let { Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT) }
        val snackBarView2 = snack?.view
        snackBarView2?.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.red_nice
            )
        )
        val params = snackBarView2?.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.BOTTOM
        params.topMargin = 100
        snackBarView2.layoutParams = params

        snack?.show()
    }

}