package com.tstudioz.fax.fme.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.feature.login.repository.UserRepositoryInterface
import com.tstudioz.fax.fme.feature.weather.codeToDisplay
import com.tstudioz.fax.fme.feature.weather.weatherSymbolKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.Locale


@InternalCoroutinesApi
class HomeViewModel(
    application: Application,
    private val repository: UserRepositoryInterface
) : AndroidViewModel(application) {

    private val _humidity = MutableLiveData<Double>()
    private val _icon = MutableLiveData<String>()
    private val _precipChance = MutableLiveData<Double>()
    private val _summary = MutableLiveData<String>()
    private val _wind = MutableLiveData<Double>()
    private val _temperature = MutableLiveData<Double>()
    private var _forecastGot = MutableLiveData<Boolean>()

    val humidity: LiveData<Double> = _humidity
    val icon: LiveData<String> = _icon
    val precipChance: LiveData<Double> = _precipChance
    val summary: LiveData<String> = _summary
    val wind: LiveData<Double> = _wind
    val temperature: LiveData<Double> = _temperature
    val forecastGot: LiveData<Boolean> = _forecastGot

    fun getForecast(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val weather = repository.fetchWeatherDetails(url)
            if (weather != null) {
                val forecastInstantDetails = weather.properties?.timeseries?.first()?.data?.instant?.details
                val forecastNextOneHours = weather.properties?.timeseries?.first()?.data?.next1Hours
                val forecastNextOneHoursDetails = forecastNextOneHours?.details
                val unparsedSummary = forecastNextOneHours?.summary?.symbolCode
                val weatherSymbol = weatherSymbolKeys[unparsedSummary]
                val iconName = "_" + weatherSymbol?.first.toString() + weatherSymbol?.second
                val summary = codeToDisplay[weatherSymbol?.first]?.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
                _humidity.postValue(forecastInstantDetails?.relativeHumidity ?: 0.0)
                _wind.postValue(forecastInstantDetails?.windSpeed ?: 0.0)
                _temperature.postValue(forecastInstantDetails?.airTemperature ?: 20.0)
                _precipChance.postValue(forecastNextOneHoursDetails?.precipitationAmount ?: 0.00)
                _summary.postValue(summary ?: "")
                _icon.postValue(iconName ?: "")
                _forecastGot.postValue(true)
            } else {
                _forecastGot.postValue(false)
            }
        }
    }
}
