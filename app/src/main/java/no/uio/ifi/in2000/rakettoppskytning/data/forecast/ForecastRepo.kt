package no.uio.ifi.in2000.rakettoppskytning.data.forecast

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.LocationForecast

class WeatherForeCastLocationRepo(){

    private val _forecast = MutableStateFlow<List<LocationForecast>>(listOf())
    fun observeForecast() : StateFlow<List<LocationForecast>> = _forecast.asStateFlow()

    suspend fun loadForecast(lat:Double, lon:Double){
        val foreCast: List<LocationForecast> = try {
            listOf( getForecast(lat,lon))


        } catch (exception: Exception) {
            listOf()
        }
        _forecast.update { foreCast }
    }

}