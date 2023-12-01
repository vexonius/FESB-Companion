package com.tstudioz.fax.fme.ui.mainscreen

import android.content.Intent
import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.Application.FESBCompanion
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.activities.MainActivity
import com.tstudioz.fax.fme.data.Repository
import com.tstudioz.fax.fme.database.Korisnik
import com.tstudioz.fax.fme.models.User
import com.tstudioz.fax.fme.util.CircularAnim
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.koin.java.KoinJavaComponent.inject
import java.io.IOException


@InternalCoroutinesApi
class LoginViewModel(application: Application)  : AndroidViewModel(application) {
    private val sharedPref = application.getSharedPreferences("PRIVATE_PREFS", MODE_PRIVATE)
    private val repository: Repository by inject(Repository::class.java)
    private var _loggedIn = MutableLiveData<Boolean>()
    val loggedIn: LiveData<Boolean>
        get() = _loggedIn

    suspend fun firstloginUser(user:User){
            repository.attemptLogin(user)
                .onStart {}.catch { println("Doslo je do pogreske") }
                .collect { result ->
                    if(result == user) {
                        val editor = sharedPref.edit()
                        editor.putBoolean("loged_in", true)
                        editor.apply()
                        val mLogRealm: Realm = Realm.getDefaultInstance()
                        try {
                            mLogRealm.executeTransaction { realm ->
                                val userrealm = realm.createObject(Korisnik::class.java)
                                userrealm.setUsername(user.username)
                                userrealm.setLozinka(user.password)
                            }
                        } finally {
                            mLogRealm.close()
                        }
                        _loggedIn.value = true
                        Log.d("hello", result.username)
                    }else if (result == User("","","")){
                        _loggedIn.value = false
                    }
                }
    }

}
