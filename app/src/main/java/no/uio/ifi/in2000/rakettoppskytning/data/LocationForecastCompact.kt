package no.uio.ifi.in2000.rakettoppskytning.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
@Serializable
data class LocationForecastCompact(
    val type: String = "",
    val geometry: Geometry,
    val properties: Properties = Properties()
)

@Serializable
data class Geometry(
    val type: String = "",
    val coordinates: List<Double> = listOf(),
)

@Serializable
data class Properties(
    val meta: Meta = Meta(),
    val timeseries: List<TimeSerie> = listOf()
)

@Serializable
data class Meta(
    val updated_at: String = "",
    val units: Units = Units()
)
@Serializable
data class Units(
    val air_pressure_at_sea_level: String = "",
    val air_temperature: String = "",
    val cloud_area_fraction: String = "",
    val precipitation_amount: String = "",
    val relative_humidity: String = "",
    val wind_from_direction: String = "",
    val wind_speed: String = ""
)

@Serializable
data class TimeSerie(
    val time: String = "",
    val data: Data = Data()
)

@Serializable
data class Data(
    val instant: Instant = Instant(),
    val next1Hours: Next_1_Hours = Next_1_Hours(),
    val next6Hours: Next_6_Hours = Next_6_Hours(),
    val next12Hours: Next_12_Hours = Next_12_Hours()
)
@Serializable
data class Summary(
    val symbol_code: String = ""
)
@Serializable
data class Details(
    val precipitation_amount: Double = 0.0
)

@Serializable
data class Next_1_Hours(
    val summary: Summary = Summary(),
    val details: Details = Details()
)
@Serializable
data class Next_6_Hours(
    val summary: Summary = Summary(),
    val details: Details = Details()
)

@Serializable
data class Next_12_Hours(
    val summary: Summary = Summary(),
    val details: Details = Details()
)

@Serializable
data class Instant(
    @SerialName("details")
    val instantDetails: InstantDetails = InstantDetails()
)

@Serializable
data class InstantDetails(
    val air_pressure_at_sea_level: Double = 0.0,
    val air_temperature: Double = 0.0,
    val cloud_area_fraction: Double = 0.0,
    val precipitation_amount: Double = 0.0,
    val relative_humidity: Double = 0.0,
    val wind_from_direction: Double = 0.0,
    val wind_speed: Double = 0.0
)

 */