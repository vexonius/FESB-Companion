package com.tstudioz.fax.fme.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.feature.login.repository.UserRepositoryInterface
import com.tstudioz.fax.fme.models.util.codeToDisplay
import com.tstudioz.fax.fme.models.util.weatherSymbolKeys
import com.tstudioz.fax.fme.weather.Forecast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.Locale


@InternalCoroutinesApi
class HomeViewModel(
    application: Application,
    private val repository: UserRepositoryInterface
) : AndroidViewModel(application) {

    /*var mForecast: Forecast? = Forecast()*/
    private var _forecast = MutableLiveData<Forecast>()
    val forecast: LiveData<Forecast> = _forecast

    private val _humidity = MutableLiveData<Double>()
    val humidity: LiveData<Double> = _humidity

    private val _icon = MutableLiveData<String>()
    val icon: LiveData<String> = _icon

    private val _precipChance = MutableLiveData<Double>()
    val precipChance: LiveData<Double> = _precipChance

    private val _summary = MutableLiveData<String>()
    val summary: LiveData<String> = _summary

    private val _wind = MutableLiveData<Double>()
    val wind: LiveData<Double> = _wind

    private val _temperature = MutableLiveData<Double>()
    val temperature: LiveData<Double> = _temperature

    private var _forecastGot = MutableLiveData<Boolean>()
    val forecastGot: LiveData<Boolean>
        get() = _forecastGot

    fun getForecast(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val test = repository.fetchWeatherDetails(url)
            if (test != null) {
                val forecastInstantDetails = test.properties?.timeseries?.first()?.data?.instant?.details
                val forecastNextOneHours = test.properties?.timeseries?.first()?.data?.next1Hours
                val forecastNextOneHoursDetails = forecastNextOneHours?.details
                val forecastNextOneHoursSummary = forecastNextOneHours?.summary
                val unparsedSummary = forecastNextOneHoursSummary?.symbolCode
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
