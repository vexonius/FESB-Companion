package com.tstudioz.fax.fme.feature.merlin.view

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.feature.database.Course
import com.tstudioz.fax.fme.feature.merlin.repository.MerlinRepositoryInterface
import com.tstudioz.fax.fme.feature.merlin.services.MerlinNetworkServiceResult
import com.tstudioz.fax.fme.models.util.PreferenceHelper.get
import com.tstudioz.fax.fme.models.util.SPKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
class MerlinViewModel(
    application: Application,
    private val repository: MerlinRepositoryInterface,
    private val sharedPreferences: SharedPreferences
) : AndroidViewModel(application) {

    var username = MutableLiveData()
    var password = MutableLiveData()
    val list : MutableLiveData<List<Course>> = MutableLiveData()
    init {
        tryUserLogin()
    }

    fun tryUserLogin() {
        val username = username.value
        val password = password.value
        viewModelScope.launch(context = Dispatchers.IO) {
            if (username != null) {
                if (password != null) {
                    when(val res = repository.login(username, password)){
                        is MerlinNetworkServiceResult.MerlinNetworkResult.Success -> {
                            list.postValue(res.data as List<Course>)
                        }
                        is MerlinNetworkServiceResult.MerlinNetworkResult.Failure -> {
                        }
                    }
                }
            }
        }
    }

}
