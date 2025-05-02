package com.tstudioz.fax.fme.feature.menza.view

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.feature.menza.MenzaResult
import com.tstudioz.fax.fme.feature.menza.models.Menza
import com.tstudioz.fax.fme.feature.menza.repository.MenzaRepositoryInterface
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch


@InternalCoroutinesApi
class MenzaViewModel(
    application: Application,
    private val repository: MenzaRepositoryInterface
) : AndroidViewModel(application) {

    private var _menza: MutableLiveData<Menza?> = MutableLiveData()
    val menza: LiveData<Menza?> = _menza

    private var _menzaOther: MutableLiveData<Menza?> = MutableLiveData()
    val menzaOther: LiveData<Menza?> = _menzaOther

    private val handler = CoroutineExceptionHandler { _, exception ->
        Log.d("MenzaViewModel", "CoroutineExceptionHandler got $exception")
    }

    fun getMenza() {
        viewModelScope.launch(Dispatchers.IO + handler) {
            when (val menza = repository.fetchMenzaDetails("fesb_vrh", true)) {
                is MenzaResult.Success -> {
                    _menza.postValue(menza.data)
                }

                is MenzaResult.Failure -> {
                }
            }
        }
    }

    fun fetchMenza(place:String) {
        viewModelScope.launch(Dispatchers.IO + handler) {
           when (val menza = repository.fetchMenzaDetails(place,false)) {
                is MenzaResult.Success -> {
                    _menzaOther.postValue(menza.data)
                }

                is MenzaResult.Failure -> {
                }
            }
        }
    }
}
