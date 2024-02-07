package com.tstudioz.fax.fme.feature.login.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.models.data.User
import com.tstudioz.fax.fme.feature.login.repository.UserRepositoryInterface
import com.tstudioz.fax.fme.feature.login.repository.models.UserRepositoryResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
class LoginViewModel(
    application: Application,
    private val repository: UserRepositoryInterface,
) : AndroidViewModel(application) {

    var loggedIn = MutableLiveData(false)
        private set

    fun tryUserLogin(user: User) {
        viewModelScope.launch(context = Dispatchers.IO) {
            when(val result = repository.attemptLogin(user.username, user.password)) {
                is UserRepositoryResult.LoginResult.Success -> {
                    loggedIn.postValue(true)
                }
                is UserRepositoryResult.LoginResult.Failure -> {
                    loggedIn.postValue(false)
                }
            }
        }
    }
}


