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
import com.tstudioz.fax.fme.networking.InternetConnectionObserver
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
    val internetAvailable: LiveData<Boolean> = InternetConnectionObserver.get()

    private val handler = CoroutineExceptionHandler { _, exception ->
        Log.d("MenzaViewModel", "CoroutineExceptionHandler got $exception")
    }

    fun getMenza() {
        if (internetAvailable.value == false) return
        viewModelScope.launch(Dispatchers.IO + handler) {
            when (val menza = repository.fetchMenzaDetails()) {
                is MenzaResult.Success -> {
                    _menza.postValue(menza.data)
                }

                is MenzaResult.Failure -> {
                }
            }
        }
    }
}
