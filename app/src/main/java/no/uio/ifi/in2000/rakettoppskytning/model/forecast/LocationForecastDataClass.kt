package no.uio.ifi.in2000.rakettoppskytning.model.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *
 * Our Nordic forecasts are updated once every hour.
 * For medium range forecasts (2–10 days) the 51 member ensemble forecast from ECMWF is used.
 * It it updated twice pr day. Horizontal resolution is approximately 18 km.
 * Air temperature, precipitation and wind speed are further post-processed to better represent local geographical features.
 *
 * */

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
    /** Dato i ISO 8601-format; år-måned-dag(T)klokkeslett:tidssone(Z) */
    val time: String,
    /** Værdata for det gitte tidspunktet */
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
data class Details (
    /**air pressure at sea level in hectoPascal */
    @SerialName("air_pressure_at_sea_level")
    val airPressureAtSeaLevel: Double,

    /**air temperature at 2m above the ground in celcius*/
    @SerialName("air_temperature")
    val airTemperature: Double,

    /** 10% of the time, the temperature is x or higher*/
    @SerialName("air_temperature_percentile_10")
    val airTemperaturePercentile10: Double,

    /** 90% of the time, the temperature is x or lower*/
    @SerialName("air_temperature_percentile_90")
    val airTemperaturePercentile90: Double,

    /** total cloud cover for all heights in %*/
    @SerialName("cloud_area_fraction")
    val cloudAreaFraction: Double,

    /**cloud cover higher than 5000m above the ground in %*/
    @SerialName("cloud_area_fraction_high")
    val cloudAreaFractionHigh: Double,

    /**cloud cover lower than 2000m above the ground in %*/
    @SerialName("cloud_area_fraction_low")
    val cloudAreaFractionLow: Double,

    /**cloud cover between 2000 and 5000m above the ground in %*/
    @SerialName("cloud_area_fraction_medium")
    val cloudAreaFractionMedium: Double,

    /** Temperatur det begynner å dugge */
    @SerialName("dew_point_temperature")
    val dewPointTemperature: Double,

    /**amount of surrounding area covered in fog (horizontal view under a 1000 meters) in %*/
    @SerialName("fog_area_fraction")
    val fogAreaFraction: Double?=null,

    /**relative humidity at 2m above the ground in %*/
    @SerialName("relative_humidity")
    val relativeHumidity: Double,

    /**ultraviolet index for cloud free conditions, 0 (low) to 11+ (extreme)*/
    @SerialName("ultraviolet_index_clear_sky")
    val ultravioletIndexClearSky: Double?=null,

    /**direction the wind is coming from (0° is north, 90° east, etc.)*/
    @SerialName("wind_from_direction")
    val windFromDirection: Double,

    /**wind speed at 10m above the ground (10 min average) in m/s*/
    @SerialName("wind_speed")
    val windSpeed: Double,

    /** Vindkast max m/s. Vindkastene er målt over 3s*/
    @SerialName("wind_speed_of_gust")
    val windSpeedOfGust: Double?=null,

    /** 10% of the time, the wind is x or lower*/
    @SerialName("wind_speed_percentile_10")
    val windSpeedPercentile10: Double,

    /** 90% of the time, the wind is x or lower*/
    @SerialName("wind_speed_percentile_90")
    val windSpeedPercentile90: Double,

):Iterable<Any?>{
     override fun iterator(): Iterator<Any?> {
        return listOf(airPressureAtSeaLevel, airTemperature, airTemperaturePercentile10,airTemperaturePercentile90,
            cloudAreaFraction,cloudAreaFractionHigh,cloudAreaFractionLow,cloudAreaFractionMedium,dewPointTemperature,fogAreaFraction,relativeHumidity,
            ultravioletIndexClearSky,windFromDirection,windSpeed,windSpeedOfGust,windSpeedPercentile10,windSpeedPercentile90).iterator()
    }

}
@Serializable
data class Next12Hours(
    val summary: Summary,
    val details: Details2,
)
@Serializable
data class Summary(
    /**see WeatherIcon product*/
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
