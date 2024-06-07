package com.tstudioz.fax.fme.feature.menza.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.database.models.Meni
import com.tstudioz.fax.fme.feature.login.repository.UserRepositoryInterface
import com.tstudioz.fax.fme.feature.menza.MenzaResult
import com.tstudioz.fax.fme.feature.weather.codeToDisplay
import com.tstudioz.fax.fme.feature.weather.weatherSymbolKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale


@InternalCoroutinesApi
class MenzaViewModel(
    application: Application,
    private val repository: UserRepositoryInterface
) : AndroidViewModel(application) {

    private var _menzaGot = MutableLiveData<Boolean>(false)
    private var _menza: MutableLiveData<List<Meni>> = MutableLiveData()
    private val _menzaError: MutableLiveData<Boolean> = MutableLiveData()

    val menzaGot: LiveData<Boolean> = _menzaGot
    val menza: LiveData<List<Meni>> = _menza
    val menzaError: LiveData<Boolean> = _menzaError

    fun getMenza(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
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
        viewModelScope.launch(Dispatchers.IO) {
            _menza.postValue(repository.readMenza())
            _menzaGot.postValue(true)
        }
    }
}
