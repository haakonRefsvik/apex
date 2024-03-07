package no.uio.ifi.in2000.rakettoppskytning

import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
    @OptIn(ExperimentalMaterial3Api::class)
    val bark = BottomSheetScaffoldState(bottomSheetState = SheetState(false, initialValue = SheetValue.PartiallyExpanded,skipHiddenState=true),snackbarHostState = SnackbarHostState())
    @OptIn(ExperimentalMaterial3Api::class)
    private val _bottomSheetScaffoldState = mutableStateOf(BottomSheetScaffoldState(bottomSheetState = bark.bottomSheetState, snackbarHostState = SnackbarHostState()))

    @OptIn(ExperimentalMaterial3Api::class)
    val bottomSheetScaffoldState: MutableState<BottomSheetScaffoldState> = _bottomSheetScaffoldState
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