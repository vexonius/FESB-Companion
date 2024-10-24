package com.tstudioz.fax.fme.feature.login.view

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.common.user.UserRepositoryInterface
import com.tstudioz.fax.fme.common.user.models.UserRepositoryResult
import com.tstudioz.fax.fme.util.PreferenceHelper.get
import com.tstudioz.fax.fme.util.PreferenceHelper.set
import com.tstudioz.fax.fme.util.SPKey
import com.tstudioz.fax.fme.util.SingleLiveEvent
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

    var loggedIn = SingleLiveEvent<Unit>()
        private set

    var errorMessage = MutableLiveData<String?>(null)
        private set

    private val handler = CoroutineExceptionHandler { _, exception ->
        errorMessage.postValue(application.getString(R.string.login_error_generic))
    }

    fun tryUserLogin() {
        var username = username.value?.trim()?.lowercase()
        val password = password.value?.trim()

        if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            errorMessage.postValue(application.getString(R.string.login_error_empty_credentials))
            return
        } else if (isEmailValid(username)) {
            // If user inputted email, get only username,
            // else continue
            username = username.substringBefore("@")
        }

        viewModelScope.launch(Dispatchers.IO + handler) {
            when (repository.attemptLogin(username, password)) {
                is UserRepositoryResult.LoginResult.Success -> {
                    loggedIn.postValue(Unit)
                }

                is UserRepositoryResult.LoginResult.Failure -> {
                    errorMessage.postValue(application.getString(R.string.login_error_invalid_credentials))
                }
            }
        }
    }

    fun checkIfFirstTimeInApp() {
        firstTimeInApp.value = sharedPreferences[SPKey.FIRST_TIME, true]
        sharedPreferences[SPKey.FIRST_TIME] = false
    }

    fun checkIfLoggedIn() {
        if (sharedPreferences[SPKey.LOGGED_IN, false]) {
            loggedIn.value = Unit
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}
