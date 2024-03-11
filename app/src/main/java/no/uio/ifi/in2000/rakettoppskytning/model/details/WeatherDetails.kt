package no.uio.ifi.in2000.rakettoppskytning.model.details

import com.google.gson.annotations.Expose
import kotlinx.serialization.Serializable
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.Data
import no.uio.ifi.in2000.rakettoppskytning.model.grib.VerticalProfile

data class WeatherDetails(
    val forecastData: Data,
    val verticalProfile: List<VerticalProfile>
)
