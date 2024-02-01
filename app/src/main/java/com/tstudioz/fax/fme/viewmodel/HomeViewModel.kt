package com.tstudioz.fax.fme.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.models.data.Repository
import com.tstudioz.fax.fme.weather.Forecast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject


@InternalCoroutinesApi
class HomeViewModel(application: Application)  : AndroidViewModel(application) {
    private val repository: Repository by inject(Repository::class.java)

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
