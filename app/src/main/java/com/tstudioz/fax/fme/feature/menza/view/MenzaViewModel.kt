package com.tstudioz.fax.fme.feature.menza.view

import android.app.Application
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@InternalCoroutinesApi
class MenzaViewModel(
    application: Application,
    private val repository: MenzaRepositoryInterface
) : AndroidViewModel(application) {

    private var _menzaGot = MutableLiveData(false)
    private var _menza: MutableLiveData<Menza?> = MutableLiveData()
    private val _menzaError: MutableLiveData<Boolean> = MutableLiveData()

    val menzaGot: LiveData<Boolean> = _menzaGot
    val menza: LiveData<Menza?> = _menza
    val menzaError: LiveData<Boolean> = _menzaError

    private val handler = CoroutineExceptionHandler { _, exception ->
        _menzaError.postValue(true)
    }

    init{
        readMenza()
    }

    fun getMenza(url: String) {
        viewModelScope.launch(Dispatchers.IO + handler) {
            when (val menza = repository.fetchMenzaDetails(url)) {
                is MenzaResult.Success -> {
                    _menza.postValue(menza.data)
                    _menzaGot.postValue(true)
                }

                is MenzaResult.Failure -> {
                    _menzaGot.postValue(false)
                    _menzaError.postValue(true)
                    delay(300)
                    _menzaError.postValue(false)
                }
            }
        }
    }

    fun readMenza() {
        viewModelScope.launch(Dispatchers.IO + handler) {
            _menza.postValue(repository.readMenza())
            _menzaGot.postValue(true)
        }
    }
}
