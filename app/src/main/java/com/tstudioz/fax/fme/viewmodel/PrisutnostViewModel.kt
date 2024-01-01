package com.tstudioz.fax.fme.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.models.data.Repository
import com.tstudioz.fax.fme.database.Predavanja
import com.tstudioz.fax.fme.models.data.User
import com.tstudioz.fax.fme.models.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject



@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class PrisutnostViewModel : ViewModel() {

    private val repository: Repository by inject(Repository::class.java)
    private var _gotPri = MutableLiveData<Boolean>()

    val gotPri: LiveData<Boolean>
        get() = _gotPri

    suspend fun fetchPrisutnost() {
        val v = repository.fetchPrisutnost()
        when (v){
            is Result.PrisutnostResult.Success -> {
                _gotPri.postValue(true)
            }
            is Result.PrisutnostResult.Failure -> {
                _gotPri.postValue(false)
            }
        }

    }

}


