package com.tstudioz.fax.fme.feature.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherFeature(
    val type: String?=null,
    val geometry: Geometry?=null,
    val properties: Properties?=null
)

@Serializable
data class Geometry(
    val type: String?=null,
    val coordinates: List<Double>?=null
)

@Serializable
data class Properties(
    val meta: Meta?=null,
    val timeseries: List<TimeSeries>?=null
)

@Serializable
data class Meta(
    val units: Units?=null,
    @SerialName("updated_at") val updatedAt: String?=null
)

@Serializable
data class Units(
    @SerialName("air_pressure_at_sea_level") val airPressureAtSeaLevel: String?=null,
    @SerialName("air_temperature") val airTemperature: String?=null,
    @SerialName("cloud_area_fraction") val cloudAreaFraction: String?=null,
    @SerialName("precipitation_amount") val precipitationAmount: String?=null,
    @SerialName("relative_humidity") val relativeHumidity: String?=null,
    @SerialName("wind_from_direction") val windFromDirection: String?=null,
    @SerialName("wind_speed") val windSpeed: String?=null,
)

@Serializable
data class TimeSeries(
    val time: String?=null,
    val data: Data?=null
)

@Serializable
data class Data(
    val instant: Instant?=null,
    @SerialName("next_12_hours") val next12Hours: Forecast?=null,
    @SerialName("next_1_hours") val next1Hours: Forecast?=null,
    @SerialName("next_6_hours") val next6Hours: Forecast?=null
)

@Serializable
data class Instant(
    val details: InstantDetails?=null
)

@Serializable
data class InstantDetails(
    @SerialName("air_pressure_at_sea_level") val airPressureAtSeaLevel: Double?=null,
    @SerialName("air_temperature") val airTemperature: Double?=null,
    @SerialName("cloud_area_fraction") val cloudAreaFraction: Double?=null,
    @SerialName("relative_humidity") val relativeHumidity: Double?=null,
    @SerialName("wind_from_direction") val windFromDirection: Double?=null,
    @SerialName("wind_speed") val windSpeed: Double?=null,
)

@Serializable
data class Forecast(
    val details: ForecastDetails?=null,
    val summary: Summary?=null
)

@Serializable
data class ForecastDetails(
    @SerialName("air_temperature_max") val airTemperatureMax: Double?=null,
    @SerialName("air_temperature_min") val airTemperatureMin: Double?=null,
    @SerialName("precipitation_amount") val precipitationAmount: Double?=null,
    @SerialName("precipitation_amount_max") val precipitationAmountMax: Double?=null,
    @SerialName("precipitation_amount_min") val precipitationAmountMin: Double?=null,
    @SerialName("probability_of_precipitation") val probabilityOfPrecipitation: Double?=null,
    @SerialName("probability_of_thunder") val probabilityOfThunder: Double?=null,
    @SerialName("ultraviolet_index_clear_sky_max") val ultravioletIndexClearSkyMax: Double?=null
)

@Serializable
data class Summary(
    @SerialName("symbol_code") val symbolCode: String?=null
)

data class WeatherDisplay(
    val location: String,
    val temperature: Double,
    val humidity: Double,
    val wind: Double,
    val precipChance: Double,
    val icon: String,
    val summary: String
)