package no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos

import no.uio.ifi.in2000.rakettoppskytning.model.forecast.Series
import no.uio.ifi.in2000.rakettoppskytning.model.grib.VerticalProfile
import no.uio.ifi.in2000.rakettoppskytning.model.historicalData.Hourly

data class WeatherAtPos(
    val weatherList: List<WeatherAtPosHour> = listOf()
)

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
    val closeToLimitScore: Double
)