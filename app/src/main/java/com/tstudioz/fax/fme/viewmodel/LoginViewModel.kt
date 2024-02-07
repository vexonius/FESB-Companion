package com.tstudioz.fax.fme.viewmodel

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.database.models.Korisnik
import com.tstudioz.fax.fme.models.data.User
import com.tstudioz.fax.fme.models.data.UserRepositoryInterface
import io.realm.kotlin.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

@InternalCoroutinesApi
class LoginViewModel(
    application: Application,
    private val repository: UserRepositoryInterface,
) : AndroidViewModel(application) {

    var loggedIn = MutableLiveData(false)
        private set

    fun tryUserLogin(user: User) {
        viewModelScope.launch(context = Dispatchers.IO) {
            val result = repository.attemptLogin(user.username, user.password)
            loggedIn.postValue(result)
        }
    }
}


