package com.tstudioz.fax.fme.feature.login.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.databinding.ActivityLoginBinding
import com.tstudioz.fax.fme.models.data.User
import com.tstudioz.fax.fme.random.NetworkUtils
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
        isUserLoggedIn
        loadBlueButton()
        helpListener()
        onBackListen()
        activateLoggedInListener(view)
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

    private fun activateLoggedInListener(view: View){
        loginViewModel.loggedIn.observe(this) { loggedIn ->
            if (loggedIn) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }else{
                showErrorSnack("Uneseni podatci su pogrešni!")
                binding.progressLogin.visibility = View.INVISIBLE
                binding.loginButton.visibility = View.VISIBLE
            }
        }
    }

    private val isUserLoggedIn: Unit
        get() {
            val sharedPreferences = getSharedPreferences("PRIVATE_PREFS", MODE_PRIVATE)  // prebacit u VM i uklonit redudanciju
            val prviPut = sharedPreferences.getBoolean("first_open", true)
            if (prviPut) {
                startActivity(Intent(this@LoginActivity, Welcome::class.java))
            }
            val prijavljen = sharedPreferences.getBoolean("logged_in", false)
            if (prijavljen) {
                val nwIntent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(nwIntent)
                finish()
            }
        }

    private fun loadBlueButton() {
        binding.loginButton.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(this@LoginActivity)) {
                val username:String = binding.loginInput.text.toString()
                val password:String = binding.loginPass.text.toString()
                if (username.isEmpty() || password.isEmpty()) {
                    showErrorSnack("Niste unijeli korisničke podatke")
                }
                else if (username.contains("@")) {
                    showErrorSnack("Potrebno je unijeti korisničko ime, ne email")
                }
                else {
                    binding.progressLogin.visibility = View.VISIBLE
                    binding.loginButton.visibility = View.INVISIBLE
                    loginViewModel.tryUserLogin(
                        User(username, password, username + "fesb.hr")
                    )

                }
            } else {
                showErrorSnack("Niste povezani")
            }
        }
    }

    private fun helpMe() {
        val dialog = Dialog(this)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.help_layout)
        dialog.setTitle("Pomoć")
        dialog.show()
    }

    private fun showErrorSnack(message: String?) {
        snack = message?.let { Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT) }
        val snackBarView2 = snack?.view
        snackBarView2?.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.red_nice
            )
        )
        val params = snackBarView2?.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        params.topMargin=100
        snackBarView2.layoutParams = params

        snack?.show()
    }

    private fun helpListener() {
        binding.loginPomoc.setOnClickListener { helpMe() }
    }
}