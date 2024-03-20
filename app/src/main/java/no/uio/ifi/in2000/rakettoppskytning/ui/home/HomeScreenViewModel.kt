package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherForeCastLocationRepo
import no.uio.ifi.in2000.rakettoppskytning.data.grib.GribRepository
import no.uio.ifi.in2000.rakettoppskytning.data.grib.getGrib
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.LocationForecast
import no.uio.ifi.in2000.rakettoppskytning.model.grib.VerticalProfile
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Favorite
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherAtPos
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherRepository

/*
data class ForeCastUiState(
    val foreCast: List<LocationForecast> = listOf()
)
data class VerticalProfileUiState(
    val verticalProfiles: List<VerticalProfile> = listOf()
)

 */

data class WeatherUiState(
    val weatherAtPos: WeatherAtPos = WeatherAtPos()
)

class HomeScreenViewModel(repo: WeatherRepository) : ViewModel() {
    private val foreCastRep = repo
    private val gribRepo = foreCastRep.gribRepository

    @OptIn(ExperimentalMaterial3Api::class)
    val scaffold = BottomSheetScaffoldState(
        bottomSheetState = SheetState(
            false,
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true
        ), snackbarHostState = SnackbarHostState()
    )

    @OptIn(ExperimentalMaterial3Api::class)
    private val _bottomSheetScaffoldState = mutableStateOf(
        BottomSheetScaffoldState(
            bottomSheetState = scaffold.bottomSheetState,
            snackbarHostState = SnackbarHostState()
        )
    )

    @OptIn(ExperimentalMaterial3Api::class)
    val bottomSheetScaffoldState: MutableState<BottomSheetScaffoldState> = _bottomSheetScaffoldState

    @RequiresApi(Build.VERSION_CODES.O)
    fun getWeatherByCord(lat: Double, lon: Double, loadHours: Int) {
        Log.d("getWeather", "apicall")
        viewModelScope.launch(Dispatchers.IO) {
            foreCastRep.loadWeather(lat, lon, loadHours)
        }
    }

    val weatherUiState: StateFlow<WeatherUiState> =
        foreCastRep.observeWeather().map { WeatherUiState(weatherAtPos = it) }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WeatherUiState()
        )

    init {
        viewModelScope.launch {
            gribRepo.loadGribFiles()
        }
    }


}