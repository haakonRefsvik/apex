package no.uio.ifi.in2000.rakettoppskytning.data
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
data class LocationForecastComplete(
    val type: String,
    val geometry: Geometry,
    val properties: Properties
)

@Serializable
data class Summary(
    /**see WeatherIcon product*/
    @SerialName("symbol_code")
    val symbolCode: String?,
)
@Serializable
data class TimeSerie(
    /** Dato i ISO 8601-format; år-måned-dag(T)klokkeslett:tidssone(Z) */
    val time: String,
    /** Værdata for det gitte tidspunktet */
    val data: Data
)
@Serializable
data class InstantDetails(
    /**air pressure at sea level in hectoPascal */
    @SerialName("air_pressure_at_sea_level")
    val pressureSeaLevel: Double,

    /**air temperature at 2m above the ground in celcius*/
    @SerialName("air_temperature")
    val airTemperature: Double,

    @SerialName("air_temperature_percentile_10")
    /** 10% of the time, the temperature is x or higher*/
    val airTemp10Percentile: Double,

    @SerialName("air_temperature_percentile_90")
    /** 90% of the time, the temperature is x or lower*/
    val airTemp90Percentile: Double,

    /** total cloud cover for all heights in %*/
    @SerialName("cloud_area_fraction")
    val cloudinessPercent:  Double,

    /**cloud cover higher than 5000m above the ground in %*/
    @SerialName("cloud_area_fraction_high")
    val cloudinessPercentHigh:  Double,

    @SerialName("cloud_area_fraction_low")
    /**cloud cover lower than 2000m above the ground in %*/
    val cloudinessPercentLow:  Double,

    @SerialName("cloud_area_fraction_medium")
    /**cloud cover between 2000 and 5000m above the ground in %*/
    val cloudinessPercentMedium:  Double,

    @SerialName("dew_point_temperature")
    /** Temperatur det begynner å dugge */
    val dewPointTemperature: Double,

    @SerialName("fog_area_fraction")
    /**amount of surrounding area covered in fog (horizontal view under a 1000 meters) in %*/
    val fogPercent:  Double ?= null,

    @SerialName("relative_humidity")
    /**relative humidity at 2m above the ground in %*/
    val relativeHumidity:  Double,

    @SerialName("ultraviolet_index_clear_sky")
    /**ultraviolet index for cloud free conditions, 0 (low) to 11+ (extreme)*/
    val uvIndexClearSky:  Double ?= null,

    @SerialName("wind_from_direction")
    /**direction the wind is coming from (0° is north, 90° east, etc.)*/
    val windFromDirection:  Double,

    @SerialName("wind_speed")
    /**wind speed at 10m above the ground (10 min average) in m/s*/
    val windSpeed:  Double,

    @SerialName("wind_speed_of_gust")
    /** Vindkast max m/s. Vindkastene er målt over 3s*/
    val windGustSpeed:  Double ?= null,

    @SerialName("wind_speed_percentile_10")
    /** 10% of the time, the wind is x or lower*/
    val windSpeed10Percentile:  Double,

    @SerialName("wind_speed_percentile_90")
    /** 90% of the time, the wind is x or lower*/
    val windSpeed90Percentile:  Double

)

/** Next_1_hours is only available in the short range forecast*/
@Serializable
data class Next1Hour(
    val summary: Summary,
    @SerialName("details")
    val details: Next_1_Hours_Details
)

/**These are aggregations or minima/maxima for a given time period, either the next 1, 6 or 12 hours.
 * Not that next_1_hours is only available in the short range forecast.*/
@Serializable
data class Next6Hours(
    val summary: Summary,
    @SerialName("details")
    val details: Next_6_Hours_Details
)

/**These are aggregations or minima/maxima for a given time period, either the next 1, 6 or 12 hours.
 * Not that next_1_hours is only available in the short range forecast.*/
@Serializable
data class Next12Hours(
    val summary: Summary,
    @SerialName("details")
    val details: Next_12_Hours_Details
)

@Serializable
data class Next_1_Hours_Details(
    /**	expected precipitation (nedbør) amount for period in mm */
    val precipitation_amount: Double,
    /** maximum likely precipitation (nedbør) for period in mm */
    val precipitation_amount_max: Double,
    /** minimum likely precipitation (nedbør) for period in mm */
    val precipitation_amount_min: Double,
    /** chance of precipitation (nedbør) during period in mm*/
    val probability_of_precipitation: Double,
    /**chance of thunder during period*/
    val probability_of_thunder: Double
)
@Serializable
data class Next_12_Hours_Details(
    /** chance of precipitation (nedbør) during period in mm*/
    val probability_of_precipitation: Double
)

@Serializable
data class Next_6_Hours_Details(
    /**maximum air temperature over period*/
    val air_temperature_max: Double,
    /**minimum air temperature over period*/
    val air_temperature_min: Double,
    /**	expected precipitation (nedbør) amount for period in mm */
    val precipitation_amount: Double,
    /** minimum likely precipitation (nedbør) for period in mm */
    val precipitation_amount_max: Double,
    /** chance of precipitation (nedbør) during period in mm*/
    val precipitation_amount_min: Double,
    /** chance of precipitation (nedbør) during period in mm*/
    val probability_of_precipitation: Double
)
@Serializable
data class Instant(
    @SerialName("details")
    val instantDetails: InstantDetails
)
@Serializable
data class Data(
    /** Weather data for a specific point in time */
    val instant: Instant,
    @SerialName("Next_1_Hours")
    val next1Hour: Next1Hour? = null,
    @SerialName("next_6_hours")
    val next6Hours: Next6Hours? = null,
    @SerialName("next_12_hours")
    val next12Hours: Next12Hours? = null,
)
@Serializable
data class Geometry(
    val type: String = "",
    val coordinates: List<Double> = listOf(),
)

@Serializable
data class Properties(
    /** Metainformasjon om dataene */
    val meta: Meta,
    /** Værdata fra og med nå (index 0) til og med 10 dager etter (siste index) */
    val timeseries: List<TimeSerie> = listOf()
)

@Serializable
data class Meta(
    val updated_at: String,
    val units: Units
)
@Serializable
data class Units(
    val air_pressure_at_sea_level: String,
    val air_temperature: String,
    val air_temperature_max: String,
    val air_temperature_min: String,
    val air_temperature_percentile_10: String,
    val air_temperature_percentile_90: String,
    val cloud_area_fraction: String,
    val cloud_area_fraction_high: String,
    val cloud_area_fraction_low: String,
    val cloud_area_fraction_medium: String,
    val dew_point_temperature: String,
    val fog_area_fraction: String,
    val precipitation_amount: String,
    val precipitation_amount_max: String,
    val precipitation_amount_min: String,
    val probability_of_precipitation: String,
    val probability_of_thunder: String,
    val relative_humidity: String,
    val ultraviolet_index_clear_sky: String,
    val wind_from_direction: String,
    val wind_speed: String,
    val wind_speed_of_gust: String,
    val wind_speed_percentile_10: String,
    val wind_speed_percentile_90: String
)

