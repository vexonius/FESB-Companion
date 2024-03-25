package com.tstudioz.fax.fme.viewmodel

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.database.models.Korisnik
import com.tstudioz.fax.fme.models.data.User
import com.tstudioz.fax.fme.models.data.UserRepositoryInterface
import io.realm.kotlin.Realm
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.core.KoinComponent
import org.koin.core.inject

@InternalCoroutinesApi
class LoginViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    private val dbManager: DatabaseManagerInterface by inject()
    private val repository: UserRepositoryInterface by inject()

    private val sharedPref = application.getSharedPreferences("PRIVATE_PREFS", MODE_PRIVATE)
    private var _loggedIn = MutableLiveData<Boolean>()
    val loggedIn: LiveData<Boolean>
        get() = _loggedIn

    suspend fun firstloginUser(user: User) {
        val result = repository.attemptLogin(user)
        if (user == result) {
            val editor = sharedPref.edit()
            editor.putBoolean("logged_in", true)
            editor.apply()

            val mLogRealm = Realm.open(dbManager.getDefaultConfiguration())
            try {
                mLogRealm.writeBlocking {
                    val newUser = Korisnik()
                    newUser.username = user.username
                    newUser.lozinka = user.password

                    this.copyToRealm(newUser)
                }
            } finally {
                mLogRealm.close()
            }
            _loggedIn.postValue(true)

            Log.d("hello", result.username)
        } else if (result == User("","",""))
        {
            _loggedIn.postValue(false)
        }
    }
}


