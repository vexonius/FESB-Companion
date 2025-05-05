package com.tstudioz.fax.fme.feature.home

val weatherSymbolKeys = mapOf(
    "clearsky_day" to Pair(1, "d"),
    "clearsky_night" to Pair(1, "n"),
    "clearsky_polartwilight" to Pair(1, "m"),
    "fair_day" to Pair(2, "d"),
    "fair_night" to Pair(2, "n"),
    "fair_polartwilight" to Pair(2, "m"),
    "partlycloudy_day" to Pair(3, "d"),
    "partlycloudy_night" to Pair(3, "n"),
    "partlycloudy_polartwilight" to Pair(3, "m"),
    "cloudy" to Pair(4, ""),
    "rainshowers_day" to Pair(5, "d"),
    "rainshowers_night" to Pair(5, "n"),
    "rainshowers_polartwilight" to Pair(5, "m"),
    "rainshowersandthunder_day" to Pair(6, "d"),
    "rainshowersandthunder_night" to Pair(6, "n"),
    "rainshowersandthunder_polartwilight" to Pair(6, "m"),
    "sleetshowers_day" to Pair(7, "d"),
    "sleetshowers_night" to Pair(7, "n"),
    "sleetshowers_polartwilight" to Pair(7, "m"),
    "snowshowers_day" to Pair(8, "d"),
    "snowshowers_night" to Pair(8, "n"),
    "snowshowers_polartwilight" to Pair(8, "m"),
    "rain" to Pair(9, ""),
    "heavyrain" to Pair(10, ""),
    "heavyrainandthunder" to Pair(11, ""),
    "sleet" to Pair(12, ""),
    "snow" to Pair(13, ""),
    "snowandthunder" to Pair(14, ""),
    "fog" to Pair(15, ""),
    "sleetshowersandthunder_day" to Pair(20, "d"),
    "sleetshowersandthunder_night" to Pair(20, "n"),
    "sleetshowersandthunder_polartwilight" to Pair(20, "m"),
    "snowshowersandthunder_day" to Pair(21, "d"),
    "snowshowersandthunder_night" to Pair(21, "n"),
    "snowshowersandthunder_polartwilight" to Pair(21, "m"),
    "rainandthunder" to Pair(22, ""),
    "sleetandthunder" to Pair(23, ""),
    "lightrainshowersandthunder_day" to Pair(24, "d"),
    "lightrainshowersandthunder_night" to Pair(24, "n"),
    "lightrainshowersandthunder_polartwilight" to Pair(24, "m"),
    "heavyrainshowersandthunder_day" to Pair(25, "d"),
    "heavyrainshowersandthunder_night" to Pair(25, "n"),
    "heavyrainshowersandthunder_polartwilight" to Pair(25, "m"),
    "lightssleetshowersandthunder_day" to Pair(26, "d"),
    "lightssleetshowersandthunder_night" to Pair(26, "n"),
    "lightssleetshowersandthunder_polartwilight" to Pair(26, "m"),
    "heavysleetshowersandthunder_day" to Pair(27, "d"),
    "heavysleetshowersandthunder_night" to Pair(27, "n"),
    "heavysleetshowersandthunder_polartwilight" to Pair(27, "m"),
    "lightssnowshowersandthunder_day" to Pair(28, "d"),
    "lightssnowshowersandthunder_night" to Pair(28, "n"),
    "lightssnowshowersandthunder_polartwilight" to Pair(28, "m"),
    "heavysnowshowersandthunder_day" to Pair(29, "d"),
    "heavysnowshowersandthunder_night" to Pair(29, "n"),
    "heavysnowshowersandthunder_polartwilight" to Pair(29, "m"),
    "lightrainandthunder" to Pair(30, ""),
    "lightsleetandthunder" to Pair(31, ""),
    "heavysleetandthunder" to Pair(32, ""),
    "lightsnowandthunder" to Pair(33, ""),
    "heavysnowandthunder" to Pair(34, ""),
    "lightrainshowers_day" to Pair(40, "d"),
    "lightrainshowers_night" to Pair(40, "n"),
    "lightrainshowers_polartwilight" to Pair(40, "m"),
    "heavyrainshowers_day" to Pair(41, "d"),
    "heavyrainshowers_night" to Pair(41, "n"),
    "heavyrainshowers_polartwilight" to Pair(41, "m"),
    "lightsleetshowers_day" to Pair(42, "d"),
    "lightsleetshowers_night" to Pair(42, "n"),
    "lightsleetshowers_polartwilight" to Pair(42, "m"),
    "heavysleetshowers_day" to Pair(43, "d"),
    "heavysleetshowers_night" to Pair(43, "n"),
    "heavysleetshowers_polartwilight" to Pair(43, "m"),
    "lightsnowshowers_day" to Pair(44, "d"),
    "lightsnowshowers_night" to Pair(44, "n"),
    "lightsnowshowers_polartwilight" to Pair(44, "m"),
    "heavysnowshowers_day" to Pair(45, "d"),
    "heavysnowshowers_night" to Pair(45, "n"),
    "heavysnowshowers_polartwilight" to Pair(45, "m"),
    "lightrain" to Pair(46, ""),
    "lightsleet" to Pair(47, ""),
    "heavysleet" to Pair(48, ""),
    "lightsnow" to Pair(49, ""),
    "heavysnow" to Pair(50, ""),
)

val codeToDisplay = mapOf(
    1 to "vedro", // clear sky
    2 to "pretežno vedro", // fair
    3 to "djelomično oblačno", // partly cloudy
    4 to "oblačno", // cloudy
    40 to "slabi pljuskovi kiše", // light rain showers
    5 to "pljuskovi kiše", // rain showers
    41 to "jaki pljuskovi kiše", // heavy rain showers
    24 to "slabi pljuskovi kiše s grmljavinom", // light rain showers and thunder
    6 to "pljuskovi kiše s grmljavinom", // rain showers and thunder
    25 to "jaki pljuskovi kiše s grmljavinom", // heavy rain showers and thunder
    42 to "slabi pljuskovi susnježice", // light sleet showers
    7 to "pljuskovi susnježice", // sleet showers
    43 to "jaki pljuskovi susnježice", // heavy sleet showers
    26 to "slabi pljuskovi susnježice s grmljavinom", // light sleet showers and thunder
    20 to "pljuskovi susnježice s grmljavinom", // sleet showers and thunder
    27 to "jaki pljuskovi susnježice s grmljavinom", // heavy sleet showers and thunder
    44 to "slabi pljuskovi snijega", // light snow showers
    8 to "pljuskovi snijega", // snow showers
    45 to "jaki pljuskovi snijega", // heavy snow showers
    28 to "slabi pljuskovi snijega s grmljavinom", // light snow showers and thunder
    21 to "pljuskovi snijega s grmljavinom", // snow showers and thunder
    29 to "jaki pljuskovi snijega s grmljavinom", // heavy snow showers and thunder
    46 to "slaba kiša", // light rain
    9 to "kiša", // rain
    10 to "jaka kiša", // heavy rain
    30 to "slaba kiša s grmljavinom", // light rain and thunder
    22 to "kiša s grmljavinom", // rain and thunder
    11 to "jaka kiša s grmljavinom", // heavy rain and thunder
    47 to "slaba susnježica", // light sleet
    12 to "susnježica", // sleet
    48 to "jaka susnježica", // heavy sleet
    31 to "slaba susnježica s grmljavinom", // light sleet and thunder
    23 to "susnježica s grmljavinom", // sleet and thunder
    32 to "jaka susnježica s grmljavinom", // heavy sleet and thunder
    49 to "slab snijeg", // light snow
    13 to "snijeg", // snow
    50 to "jak snijeg", // heavy snow
    33 to "slab snijeg s grmljavinom", // light snow and thunder
    14 to "snijeg s grmljavinom", // snow and thunder
    34 to "jak snijeg s grmljavinom", // heavy snow and thunder
    15 to "magla" // fog
)

