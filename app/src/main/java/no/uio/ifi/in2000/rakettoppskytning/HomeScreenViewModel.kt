package no.uio.ifi.in2000.rakettoppskytning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ForeCastUiState(val foreCast: List<LocationForecast> = listOf())
class HomeScreenViewModel : ViewModel(){
    var hasBeenCalled = false
    private val foreCastRep: WeatherForeCastLocationRepo = WeatherForeCastLocationRepo()

    val foreCastUiState: StateFlow<ForeCastUiState> = foreCastRep.observeForecast().map{ForeCastUiState(foreCast = it)}.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ForeCastUiState()
    )
    fun getForecastByCord(lat: Double, lon:Double){
        viewModelScope.launch {
            foreCastRep.loadForecast(lat,lon)}

    }
//    init {
//        viewModelScope.launch {
//            foreCastRep.loadForecast(59.84,10.78)}
//
//
//    }

}