package com.tstudioz.fax.fme.weather

import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class Current {
    var icon: String? = null
        get() = field
        set
    var time: Long = 0
    private var mTemperature = 0.0
    private var mWind = 0.0
    var humidity = 0.0
    private var mPrecipChance = 0.0
    var summary: String? = null
    var timeZone: String? = null
    val formattedTime: String
        get() {
            val formatter = SimpleDateFormat("h:mm a")
            formatter.timeZone = TimeZone.getTimeZone(timeZone)
            val dateTime = Date(time * 1000)
            return formatter.format(dateTime)
        }
    val temperature: Int
        get() = Math.round(mTemperature).toInt()

    fun setTemperature(temperature: Double) {
        mTemperature = temperature
    }

    var precipChance: Double
        get() = mPrecipChance
        set(precipChance) {
            mPrecipChance = precipChance
        }
    var wind: Double
        get() = mWind
        set(wind) {
            mWind = wind
        }
}