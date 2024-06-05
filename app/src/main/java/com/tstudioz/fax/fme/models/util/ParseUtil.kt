package com.tstudioz.fax.fme.models.util

import com.tstudioz.fax.fme.weather.Current
import org.json.JSONObject

fun parseWeatherDetails(data: String): Current {
    val forecastjson = JSONObject(data)

    val currently0 = forecastjson.getJSONObject("properties")
    val currentlyArray = currently0.getJSONArray("timeseries")
    val currently =
        currentlyArray.getJSONObject(0).getJSONObject("data").getJSONObject("instant").getJSONObject("details")
    val currentlyNextOneHours = currentlyArray.getJSONObject(0).getJSONObject("data").getJSONObject("next_1_hours")
    val currentlyNextOneHoursSummary = currentlyNextOneHours.getJSONObject("summary")
    val currentlyNextOneHoursDetails = currentlyNextOneHours.getJSONObject("details")
    val unparsedsummary = currentlyNextOneHoursSummary.getString("symbol_code")
    val summarycode: String? = if (unparsedsummary.contains("_")) {
        unparsedsummary.substring(0, unparsedsummary.indexOf('_'))
    } else {
        unparsedsummary
    }

    val current = Current()
    current.humidity = currently.getDouble("relative_humidity")
    current.icon = currentlyNextOneHoursSummary.getString("symbol_code")
    current.precipChance = currentlyNextOneHoursDetails.getDouble("precipitation_amount")
    current.summary = summarycode
    current.wind = currently.getDouble("wind_speed")
    current.setTemperature(currently.getDouble("air_temperature"))
    return current
}
