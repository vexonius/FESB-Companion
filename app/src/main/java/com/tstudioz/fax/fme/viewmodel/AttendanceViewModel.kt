package com.tstudioz.fax.fme.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.database.models.Dolazak
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.models.data.User
import com.tstudioz.fax.fme.models.data.UserRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class AttendanceViewModel : ViewModel(), KoinComponent {

    private val repository: UserRepositoryInterface by inject()
    var shouldShow = MutableLiveData(true)
        private set

    fun fetchAttendance(user: User) {
        viewModelScope.launch(context = Dispatchers.IO) {
            when (val attendance = repository.fetchAttendance(user)) {
                is NetworkServiceResult.PrisutnostResult.Success -> {
                    insert(attendance.pris)
                    shouldShow.postValue(true)
                }

                is NetworkServiceResult.PrisutnostResult.Failure -> {
                    shouldShow.postValue(false)
                }
            }
        }
    }

    private suspend fun insert(attendance: List<Dolazak>){
        repository.insertAttendance(attendance)
    }

}


