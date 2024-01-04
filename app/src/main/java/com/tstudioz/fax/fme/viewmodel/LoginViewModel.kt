package com.tstudioz.fax.fme.viewmodel

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tstudioz.fax.fme.models.data.Repository
import com.tstudioz.fax.fme.database.Korisnik
import com.tstudioz.fax.fme.models.data.User
import io.realm.Realm
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.java.KoinJavaComponent.inject


@InternalCoroutinesApi
class LoginViewModel(application: Application)  : AndroidViewModel(application) {
    private val sharedPref = application.getSharedPreferences("PRIVATE_PREFS", MODE_PRIVATE)
    private val repository: Repository by inject(Repository::class.java)
    private var _loggedIn = MutableLiveData<Boolean>()
    val loggedIn: LiveData<Boolean>
        get() = _loggedIn

    suspend fun firstloginUser(user: User) {
        val result = repository.attemptLogin(user)
        if(result == user)
        {
            val editor = sharedPref.edit()
            editor.putBoolean("logged_in", true)
            editor.apply()
            val mLogRealm: Realm = Realm.getDefaultInstance()
            try {
                mLogRealm.executeTransaction { realm ->
                    val userrealm = realm.createObject(Korisnik::class.java)
                    userrealm.username = user.username
                    userrealm.lozinka = user.password
                }
            } finally {
                mLogRealm.close()
            }
            _loggedIn.postValue(true)

            Log.d("hello", result.username)
        }else if (result == User("","",""))
        {
            _loggedIn.postValue(false)
        }
    }
}


