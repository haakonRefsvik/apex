package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.util.Log
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherForeCastLocationRepo
import no.uio.ifi.in2000.rakettoppskytning.data.grib.GribRepository
import no.uio.ifi.in2000.rakettoppskytning.data.grib.getGrib
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.LocationForecast
import no.uio.ifi.in2000.rakettoppskytning.model.grib.VerticalProfile

data class ForeCastUiState(
    val foreCast: List<LocationForecast> = listOf()
)
data class VerticalProfileUiState(
    val verticalProfiles: List<VerticalProfile> = listOf()
)

data class MapUIState @OptIn(MapboxExperimental::class) constructor(
    val mapViewportState: MapViewportState = MapViewportState()
)


class HomeScreenViewModel(repo: WeatherForeCastLocationRepo) : ViewModel() {
    private val foreCastRep = repo

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
    private val _lat = mutableDoubleStateOf(59.9434927)
    private val _lon = mutableDoubleStateOf(10.71181022)


    val lat: MutableState<Double> = _lat
    val lon: MutableState<Double> = _lon


    val foreCastUiState: StateFlow<ForeCastUiState> =
        foreCastRep.observeForecast().map { ForeCastUiState(foreCast = it) }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ForeCastUiState()
        )

    fun getForecastByCord(lat: Double, lon: Double) {
        Log.d("getForecastByCord", "apicall")
        viewModelScope.launch(Dispatchers.IO) {
            foreCastRep.loadForecast(lat, lon)
        }
    }

    private val gribRepo: GribRepository = GribRepository()

    fun getVerticalProfileByCord(lat: Double, lon: Double) {
        Log.d("getVerticalProfileByCord", "apicall")
        viewModelScope.launch(Dispatchers.IO) {
            foreCastRep.loadVerticalProfiles(lat, lon)
        }
    }

    val verticalProfileUiState: StateFlow<VerticalProfileUiState> =
        foreCastRep.observeVerticalProfiles().map { VerticalProfileUiState(verticalProfiles = it) }
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = VerticalProfileUiState()
            )


    init {
        viewModelScope.launch {
            gribRepo.loadGribFiles()
        }
    }


}