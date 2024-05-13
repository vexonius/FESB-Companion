package com.tstudioz.fax.fme.feature.login.view

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.feature.login.repository.UserRepositoryInterface
import com.tstudioz.fax.fme.feature.login.repository.models.UserRepositoryResult
import com.tstudioz.fax.fme.models.util.PreferenceHelper.get
import com.tstudioz.fax.fme.models.util.SPKey
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
class LoginViewModel(
    private val application: Application,
    private val repository: UserRepositoryInterface,
    private val sharedPreferences: SharedPreferences
) : AndroidViewModel(application) {

    var username = MutableLiveData("")
    var password = MutableLiveData("")

    var firstTimeInApp = MutableLiveData(false)
        private set

    var loggedIn = MutableLiveData(false)
        private set

    var errorMessage = MutableLiveData<String?>(null)
        private set

    private val handler = CoroutineExceptionHandler { _, exception ->
        errorMessage.postValue(application.getString(R.string.login_error_generic))
    }

    fun tryUserLogin() {
        var username = username.value
        val password = password.value

        if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            errorMessage.postValue("Niste unijeli korisničke podatke")
            return
        } else if (isEmailValid(username)) {
            // If user inputted email, get only username,
            // else continue
            username = username.substringBefore("@")
        }

        viewModelScope.launch(Dispatchers.IO + handler) {
            when (repository.attemptLogin(username, password)) {
                is UserRepositoryResult.LoginResult.Success -> {
                    loggedIn.postValue(true)
                }

                is UserRepositoryResult.LoginResult.Failure -> {
                    loggedIn.postValue(false)
                    errorMessage.postValue(application.getString(R.string.login_error_invalid_credentials))
                }
            }
        }
    }

    fun checkIfFirstTimeInApp() {
        firstTimeInApp.value = sharedPreferences[SPKey.FIRST_TIME, true]
    }

    fun checkIfLoggedIn() {
        loggedIn.value = sharedPreferences[SPKey.LOGGED_IN, false]
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}
