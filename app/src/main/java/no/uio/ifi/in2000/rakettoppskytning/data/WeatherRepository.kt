package no.uio.ifi.in2000.rakettoppskytning.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class WeatherRepository {
    private val forecast = MutableStateFlow(LocationForecastComplete)
    suspend fun loadData(){
    }
}