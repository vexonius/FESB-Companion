package com.tstudioz.fax.fme.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.tstudioz.fax.fme.Application.FESBCompanion.Companion.instance
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.Korisnik
import com.tstudioz.fax.fme.databinding.ActivityLoginBinding
import com.tstudioz.fax.fme.models.User
import com.tstudioz.fax.fme.networking.NetworkUtils
import com.tstudioz.fax.fme.ui.mainscreen.LoginViewModel
import com.tstudioz.fax.fme.util.CircularAnim
import io.realm.Realm
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody.Builder
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException


class LoginActivity : AppCompatActivity() {
    private var snack: Snackbar? = null
    private lateinit var binding: ActivityLoginBinding
    @OptIn(InternalCoroutinesApi::class)
    lateinit var loginViewModel: LoginViewModel

    @OptIn(InternalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        loginViewModel = LoginViewModel(application)
        val view: View = binding.root
        setContentView(view)
        isUserLoggedIn
        loadBlueButton()
        helpListener()
        onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val a = Intent(Intent.ACTION_MAIN)
                a.addCategory(Intent.CATEGORY_HOME)
                a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(a)
            }
        })
        activateLoggedInListener(view)

    }
    @OptIn(InternalCoroutinesApi::class)
    private fun activateLoggedInListener(view: View){
        loginViewModel.loggedIn.observe(this) { loggedIn ->
            if (loggedIn) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                CircularAnim.fullActivity(this@LoginActivity, view)
                    .colorOrImageRes(R.color.colorAccent)
                    .go {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
            }else{
                showErrorSnack("Uneseni podatci su pogrešni!")
                binding.progressLogin.visibility = View.INVISIBLE
                binding.loginButton.visibility = View.VISIBLE
            }
        }
    }

    private val isUserLoggedIn: Unit
        get() {
            val sharedPreferences = getSharedPreferences("PRIVATE_PREFS", MODE_PRIVATE)
            val prviPut = sharedPreferences.getBoolean("first_open", true)
            if (prviPut) {
                startActivity(Intent(this@LoginActivity, Welcome::class.java))
            }
            val prijavljen = sharedPreferences.getBoolean("loged_in", false)
            if (prijavljen) {
                val nwIntent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(nwIntent)
                finish()
            }
        }

    @OptIn(InternalCoroutinesApi::class)
    private fun loadBlueButton() {
        binding.loginButton.setOnClickListener { view ->
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
                    lifecycleScope.launch{
                        loginViewModel.firstloginUser(
                            User(username, password, username + "fesb.hr"))
                    }
            }
            } else {
                showErrorSnack("Niste povezani")
            }
        }
    }


    private fun helpMe() {
        val dialog = Dialog(this)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.help_layout)
        dialog.setTitle("Pomoć")
        dialog.show()
    }

    fun showErrorSnack(message: String?) {
        snack = Snackbar.make(binding.root, message!!, Snackbar.LENGTH_SHORT)
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

    @OptIn(InternalCoroutinesApi::class)
    fun validateUser(user: String, pass: String, mView: View?) {
        val okHttpClient = instance!!.okHttpInstance
        val formData: RequestBody = Builder()
            .add("Username", user)
            .add("Password", pass)
            .add("IsRememberMeChecked", "true")
            .build()
        val rq: Request = Request.Builder()
            .url("https://korisnik.fesb.unist.hr/prijava")
            .post(formData)
            .build()
        val call0 = okHttpClient!!.newCall(rq)
        call0.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("pogreska", "failure")
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.request.url.toString() == "https://korisnik.fesb.unist.hr/") {
                    runOnUiThread { register(user, pass, mView) }
                } else {
                    runOnUiThread {
                        showErrorSnack("Uneseni podatci su pogrešni!")
                        binding.progressLogin.visibility = View.INVISIBLE
                        binding.loginButton.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    fun register(username: String?, password: String?, nView: View?) {
        val sharedPref = getSharedPreferences("PRIVATE_PREFS", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("loged_in", true)
        editor.apply()
        val mLogRealm: Realm = Realm.getDefaultInstance()
        try {
            mLogRealm.executeTransaction { realm ->
                val user = realm.createObject(Korisnik::class.java)
                user.setUsername(username)
                user.setLozinka(password)
            }
        }
        finally {
            mLogRealm.close()
        }
        if (nView != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(nView.windowToken, 0)
        }
        CircularAnim.fullActivity(this@LoginActivity, nView)
            .colorOrImageRes(R.color.colorAccent)
            .go {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
    }

    private fun helpListener() {
        binding.loginPomoc.setOnClickListener { helpMe() }
    }
}