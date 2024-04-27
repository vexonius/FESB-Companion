package com.tstudioz.fax.fme.feature.login.view

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.feature.login.repository.UserRepositoryInterface
import com.tstudioz.fax.fme.feature.login.repository.models.UserRepositoryResult
import com.tstudioz.fax.fme.models.util.PreferenceHelper.get
import com.tstudioz.fax.fme.models.util.SPKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
class LoginViewModel(
    application: Application,
    private val repository: UserRepositoryInterface,
    private val sharedPreferences: SharedPreferences
) : AndroidViewModel(application) {

    var firstTimeInApp = MutableLiveData(false)
        private set

    var loggedIn = MutableLiveData(false)
        private set

    var errorMessage = MutableLiveData<String?>(null)
        private set

    var username = MutableLiveData("")
    var password = MutableLiveData("")

    fun tryUserLogin() {
        val username = username.value
        val password = password.value

        if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            errorMessage.postValue("Niste unijeli korisničke podatke")
            return
        }
        else if (username.contains("@")) {
            errorMessage.postValue("Potrebno je unijeti korisničko ime, ne email")
            return
        }

        viewModelScope.launch(context = Dispatchers.IO) {
            when(repository.attemptLogin(username, password)) {
                is UserRepositoryResult.LoginResult.Success -> {
                    loggedIn.postValue(true)
                }
                is UserRepositoryResult.LoginResult.Failure -> {
                    loggedIn.postValue(false)
                    errorMessage.postValue("Uneseni podatci su pogrešni")
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

}
