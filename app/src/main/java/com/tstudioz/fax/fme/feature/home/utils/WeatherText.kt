package com.tstudioz.fax.fme.feature.home.utils


import com.tstudioz.fax.fme.R

fun getWeatherText(key: String?): Int {
    return when (key) {
            "clearsky" -> R.string.weather_clearsky
            "fair" -> R.string.weather_fair
            "partlycloudy" -> R.string.weather_partlycloudy
            "cloudy" -> R.string.weather_cloudy
            "rainshowers" -> R.string.weather_rainshowers
            "rainshowersandthunder" -> R.string.weather_rainshowersandthunder
            "sleetshowers" -> R.string.weather_sleetshowers
            "snowshowers" -> R.string.weather_snowshowers
            "rain" -> R.string.weather_rain
            "heavyrain" -> R.string.weather_heavyrain
            "heavyrainandthunder" -> R.string.weather_heavyrainandthunder
            "sleet" -> R.string.weather_sleet
            "snow" -> R.string.weather_snow
            "snowandthunder" -> R.string.weather_snowandthunder
            "fog" -> R.string.weather_fog
            "sleetshowersandthunder" -> R.string.weather_sleetshowersandthunder
            "snowshowersandthunder" -> R.string.weather_snowshowersandthunder
            "rainandthunder" -> R.string.weather_rainandthunder
            "sleetandthunder" -> R.string.weather_sleetandthunder
            "lightrainshowersandthunder" -> R.string.weather_lightrainshowersandthunder
            "heavyrainshowersandthunder" -> R.string.weather_heavyrainshowersandthunder
            "lightssleetshowersandthunder" -> R.string.weather_lightssleetshowersandthunder
            "heavysleetshowersandthunder" -> R.string.weather_heavysleetshowersandthunder
            "lightssnowshowersandthunder" -> R.string.weather_lightssnowshowersandthunder
            "heavysnowshowersandthunder" -> R.string.weather_heavysnowshowersandthunder
            "lightrainandthunder" -> R.string.weather_lightrainandthunder
            "lightsleetandthunder" -> R.string.weather_lightsleetandthunder
            "heavysleetandthunder" -> R.string.weather_heavysleetandthunder
            "lightsnowandthunder" -> R.string.weather_lightsnowandthunder
            "heavysnowandthunder" -> R.string.weather_heavysnowandthunder
            "lightrainshowers" -> R.string.weather_lightrainshowers
            "heavyrainshowers" -> R.string.weather_heavyrainshowers
            "lightsleetshowers" -> R.string.weather_lightsleetshowers
            "heavysleetshowers" -> R.string.weather_heavysleetshowers
            "lightsnowshowers" -> R.string.weather_lightsnowshowers
            "heavysnowshowers" -> R.string.weather_heavysnowshowers
            "lightrain" -> R.string.weather_lightrain
            "lightsleet" -> R.string.weather_lightsleet
            "heavysleet" -> R.string.weather_heavysleet
            "lightsnow" -> R.string.weather_lightsnow
            "heavysnow" -> R.string.weather_heavysnow
            else -> R.string.weather_clearsky // fallback
        }
}