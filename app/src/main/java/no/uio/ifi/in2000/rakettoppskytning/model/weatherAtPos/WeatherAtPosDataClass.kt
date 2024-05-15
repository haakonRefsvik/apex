package no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.Series
import no.uio.ifi.in2000.rakettoppskytning.model.grib.VerticalProfile

/**
 * WeatherData serves as a base for weather-related data structures.
 * */
open class WeatherData(
    open val weatherList: List<WeatherAtPosHour> = listOf()
)

/**
 * WeatherAtPos encapsulates weather data for a specific position.
 * */
data class WeatherAtPos(
    override val weatherList: List<WeatherAtPosHour> = listOf()
) : WeatherData()

/**
 * WeatherFavorites stores weather data marked as favorites.
 * */
data class WeatherFavorites(
    override val weatherList: List<WeatherAtPosHour> = listOf()
) : WeatherData()


/**
 * WeatherAtPosHour represents weather data at a particular position and time,
 * including various parameters like temperature and wind speed.
 * */
data class WeatherAtPosHour(
    val date: String,
    val hour: Int,
    val lat: Double,
    val lon: Double,
    val series: Series,
    val verticalProfile: VerticalProfile?,
    /** Moisture in the soil in %*/
    val soilMoisture: Int?,
    val valuesToLimitMap: HashMap<String, Double>,
    val closeToLimitScore: Double,
    val favorite: MutableState<Boolean> = mutableStateOf(false)
)