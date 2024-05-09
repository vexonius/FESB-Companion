package com.tstudioz.fax.fme.feature.merlin.view

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.feature.merlin.database.Course
import com.tstudioz.fax.fme.feature.merlin.database.CourseDetails
import com.tstudioz.fax.fme.feature.merlin.repository.MerlinRepositoryInterface
import com.tstudioz.fax.fme.feature.merlin.services.MerlinNetworkServiceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
class MerlinViewModel(
    application: Application,
    private val repository: MerlinRepositoryInterface,
    private val sharedPreferences: SharedPreferences
) : AndroidViewModel(application) {

    var username = MutableLiveData(sharedPreferences.getString("username", ""))
    var password = MutableLiveData(sharedPreferences.getString("password", ""))
    val list: MutableLiveData<List<Course>> = MutableLiveData()
    val list2 = MutableLiveData<List<CourseDetails>>()

    init {
        tryUserLogin()
    }

    fun tryUserLogin() {
        val username = username.value
        val password = password.value
        viewModelScope.launch(context = Dispatchers.IO) {
            if (username != null) {
                if (password != null) {
                    when (val res = repository.login("$username@fesb.hr", password)) {
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

    fun getCourseData(id: Int) {
        viewModelScope.launch(context = Dispatchers.IO) {
            when (val res = repository.getCourseDetails(id)) {
                is MerlinNetworkServiceResult.MerlinNetworkResult.Success -> {
                    list2.postValue(res.data as List<CourseDetails>)
                    list.postValue(emptyList())
                }

                is MerlinNetworkServiceResult.MerlinNetworkResult.Failure -> {
                }
            }
        }
    }

}
