package com.tstudioz.fax.fme.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.feature.login.repository.UserRepositoryInterface
import com.tstudioz.fax.fme.weather.Forecast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch


@InternalCoroutinesApi
class HomeViewModel(application: Application,
                    private val repository: UserRepositoryInterface
) : AndroidViewModel(application) {

    var mForecast: Forecast? = Forecast()
    private var _forecastGot = MutableLiveData<Boolean>()
    val forecastGot: LiveData<Boolean>
        get() = _forecastGot

    fun getForecast(url: String) {
        viewModelScope.launch(Dispatchers.IO){
            mForecast?.current = repository.fetchWeatherDetails(url)
            if (mForecast?.current != null) {
                _forecastGot.postValue(true)
            } else {
                _forecastGot.postValue(false)
            }
        }
    }
}
