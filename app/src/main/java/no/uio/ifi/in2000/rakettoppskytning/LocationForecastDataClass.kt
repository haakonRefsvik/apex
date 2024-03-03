package no.uio.ifi.in2000.rakettoppskytning

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationForecast(
    val type: String,
    val geometry: Geometry,
    val properties: Properties,
)
@Serializable
data class Geometry(
    val type: String,
    val coordinates: List<Double>,
)
@Serializable
data class Properties(
    val meta: Meta,
    val timeseries: List<Series>,
)
@Serializable
data class Meta(
    @SerialName("updated_at")
    val updatedAt: String,
    val units: Units,
)
@Serializable
data class Units(
    @SerialName("air_pressure_at_sea_level")
    val airPressureAtSeaLevel: String,
    @SerialName("air_temperature")
    val airTemperature: String,
    @SerialName("air_temperature_max")
    val airTemperatureMax: String,
    @SerialName("air_temperature_min")
    val airTemperatureMin: String,
    @SerialName("air_temperature_percentile_10")
    val airTemperaturePercentile10: String,
    @SerialName("air_temperature_percentile_90")
    val airTemperaturePercentile90: String,
    @SerialName("cloud_area_fraction")
    val cloudAreaFraction: String,
    @SerialName("cloud_area_fraction_high")
    val cloudAreaFractionHigh: String,
    @SerialName("cloud_area_fraction_low")
    val cloudAreaFractionLow: String,
    @SerialName("cloud_area_fraction_medium")
    val cloudAreaFractionMedium: String? = null,
    @SerialName("dew_point_temperature")
    val dewPointTemperature: String? = null,
    @SerialName("fog_area_fraction")
    val fogAreaFraction: String? = null,
    @SerialName("precipitation_amount")
    val precipitationAmount: String,
    @SerialName("precipitation_amount_max")
    val precipitationAmountMax: String,
    @SerialName("precipitation_amount_min")
    val precipitationAmountMin: String,
    @SerialName("probability_of_precipitation")
    val probabilityOfPrecipitation: String,
    @SerialName("probability_of_thunder")
    val probabilityOfThunder: String,
    @SerialName("relative_humidity")
    val relativeHumidity: String,
    @SerialName("ultraviolet_index_clear_sky")
    val ultravioletIndexClearSky: String,
    @SerialName("wind_from_direction")
    val windFromDirection: String,
    @SerialName("wind_speed")
    val windSpeed: String,
    @SerialName("wind_speed_of_gust")
    val windSpeedOfGust: String,
    @SerialName("wind_speed_percentile_10")
    val windSpeedPercentile10: String,
    @SerialName("wind_speed_percentile_90")
    val windSpeedPercentile90: String,
)

@Serializable
data class Series(
    val time: String,
    val data: Data,
)
@Serializable
data class Data(
    val instant: Instant,
    @SerialName("next_12_hours")
    val next12Hours: Next12Hours?= null,
    @SerialName("next_1_hours")
    val next1Hours: Next1Hours?= null,
    @SerialName("next_6_hours")
    val next6Hours: Next6Hours?= null,
)
@Serializable
data class Instant(
    val details: Details,
)
@Serializable
data class Details(
    @SerialName("air_pressure_at_sea_level")
    val airPressureAtSeaLevel: Double,
    @SerialName("air_temperature")
    val airTemperature: Double,
    @SerialName("air_temperature_percentile_10")
    val airTemperaturePercentile10: Double,
    @SerialName("air_temperature_percentile_90")
    val airTemperaturePercentile90: Double,
    @SerialName("cloud_area_fraction")
    val cloudAreaFraction: Double,
    @SerialName("cloud_area_fraction_high")
    val cloudAreaFractionHigh: Double,
    @SerialName("cloud_area_fraction_low")
    val cloudAreaFractionLow: Double,
    @SerialName("cloud_area_fraction_medium")
    val cloudAreaFractionMedium: Double,
    @SerialName("dew_point_temperature")
    val dewPointTemperature: Double,
    @SerialName("fog_area_fraction")
    val fogAreaFraction: Double?=null,
    @SerialName("relative_humidity")
    val relativeHumidity: Double,
    @SerialName("ultraviolet_index_clear_sky")
    val ultravioletIndexClearSky: Double?=null,
    @SerialName("wind_from_direction")
    val windFromDirection: Double,
    @SerialName("wind_speed")
    val windSpeed: Double,
    @SerialName("wind_speed_of_gust")
    val windSpeedOfGust: Double?=null,
    @SerialName("wind_speed_percentile_10")
    val windSpeedPercentile10: Double,
    @SerialName("wind_speed_percentile_90")
    val windSpeedPercentile90: Double,
)
@Serializable
data class Next12Hours(
    val summary: Summary,
    val details: Details2,
)
@Serializable
data class Summary(
    @SerialName("symbol_code")
    val symbolCode: String,
    @SerialName("symbol_confidence")
    val symbolConfidence: String,
)
@Serializable
data class Details2(
    @SerialName("probability_of_precipitation")
    val probabilityOfPrecipitation: Double,
)
@Serializable
data class Next1Hours(
    val summary: Summary2,
    val details: Details3,
)
@Serializable
data class Summary2(
    @SerialName("symbol_code")
    val symbolCode: String,
)
@Serializable
data class Details3(
    @SerialName("precipitation_amount")
    val precipitationAmount: Double,
    @SerialName("precipitation_amount_max")
    val precipitationAmountMax: Double,
    @SerialName("precipitation_amount_min")
    val precipitationAmountMin: Double,
    @SerialName("probability_of_precipitation")
    val probabilityOfPrecipitation: Double,
    @SerialName("probability_of_thunder")
    val probabilityOfThunder: Double,
)
@Serializable
data class Next6Hours(
    val summary: Summary3,
    val details: Details4,
)
@Serializable
data class Summary3(
    @SerialName("symbol_code")
    val symbolCode: String,
)
@Serializable
data class Details4(
    @SerialName("air_temperature_max")
    val airTemperatureMax: Double,
    @SerialName("air_temperature_min")
    val airTemperatureMin: Double,
    @SerialName("precipitation_amount")
    val precipitationAmount: Double,
    @SerialName("precipitation_amount_max")
    val precipitationAmountMax: Double,
    @SerialName("precipitation_amount_min")
    val precipitationAmountMin: Double,
    @SerialName("probability_of_precipitation")
    val probabilityOfPrecipitation: Double,
)
