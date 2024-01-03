package com.tstudioz.fax.fme.viewmodel

import android.app.Application
import android.content.ContentValues
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tstudioz.fax.fme.models.data.Repository
import com.tstudioz.fax.fme.weather.Current
import com.tstudioz.fax.fme.weather.Forecast
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import org.koin.java.KoinJavaComponent.inject
import java.io.IOException

import okhttp3.OkHttpClient


@InternalCoroutinesApi
class HomeViewModel(application: Application)  : AndroidViewModel(application) {
    private val sharedPref = application.getSharedPreferences("PRIVATE_PREFS", MODE_PRIVATE)
    private val repository: Repository by inject(Repository::class.java)

    var mForecast: Forecast? = Forecast()
    private var _forecastGot = MutableLiveData<Boolean>()
    val forecastGot: LiveData<Boolean>
        get() = _forecastGot
    private val client: OkHttpClient by inject(OkHttpClient::class.java)

    fun getForecast(url: String) {
        val request: Request = Request.Builder()
            .url(url).header("Accept", "application/xml").header("User-Agent", "FesbCompanion/1.0")
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(ContentValues.TAG, "Exception caught", e)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                try {
                    val jsonData = response.body?.string()
                    if (response.isSuccessful) {
                        mForecast?.current = jsonData?.let { parseForecastDetails(it) }
                        _forecastGot.postValue(true)
                    } else {
                        _forecastGot.postValue(false)
                    }
                } catch (e: IOException) {
                    Log.e(ContentValues.TAG, "Exception caught: ", e)
                } catch (e: JSONException) {
                    Log.e(ContentValues.TAG, "Exception caught: ", e)
                }finally {
                    response.body?.close()
                }
            }
        })
    }

    @Throws(JSONException::class)
    private fun parseForecastDetails(jsonData: String): Current {
        val forecastjson = JSONObject(jsonData)

        val currently0 = forecastjson.getJSONObject("properties")
        val currentlyArray = currently0.getJSONArray("timeseries")
        val currently = currentlyArray.getJSONObject(0).getJSONObject("data").getJSONObject("instant").getJSONObject("details")
        val currentlyNextOneHours = currentlyArray.getJSONObject(0).getJSONObject("data").getJSONObject("next_1_hours")
        val currentlyNextOneHoursSummary = currentlyNextOneHours.getJSONObject("summary")
        val currentlyNextOneHoursDetails = currentlyNextOneHours.getJSONObject("details")
        val unparsedsummary = currentlyNextOneHoursSummary.getString("symbol_code")
        val summarycode: String? = if (unparsedsummary.contains("_"))
        { unparsedsummary.substring(0, unparsedsummary.indexOf('_'))
        } else { unparsedsummary }

        val current = Current()
        current.humidity = currently.getDouble("relative_humidity")
        current.icon = currentlyNextOneHoursSummary.getString("symbol_code")
        current.precipChance = currentlyNextOneHoursDetails.getDouble("precipitation_amount")
        current.summary = summarycode
        current.wind = currently.getDouble("wind_speed")
        current.setTemperature(currently.getDouble("air_temperature"))
        return current
    }
}
