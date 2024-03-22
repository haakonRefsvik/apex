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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import no.uio.ifi.in2000.rakettoppskytning.data.database.FavoriteDao
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherAtPos
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherRepository
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Favorite
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteEvent
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteState

data class WeatherUiState(
    val weatherAtPos: WeatherAtPos = WeatherAtPos()
)

class HomeScreenViewModel(repo: WeatherRepository, private val dao: FavoriteDao) : ViewModel() {
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


    private val _favorites =
        dao.getFavorites().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(FavoriteState())
    val state = combine(_state, _favorites) { state, favorites ->
        state.copy(
            favorites = favorites
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FavoriteState())

    fun onEvent(event: FavoriteEvent) {
        when (event) {
            is FavoriteEvent.DeleteFavorite -> {
                viewModelScope.launch {
                    dao.deleteFavorite(event.favorite)
                }
            }

            FavoriteEvent.HideDialog -> {
                _state.update {
                    it.copy(
                        isAddingFavorite = false,
                        name = "",
                        lat = "",
                        lon = ""
                    )
                }
            }

            FavoriteEvent.SaveFavorite -> {
                val name = state.value.name
                val lat = state.value.lat
                val lon = state.value.lon

                if (name.isBlank() || lat.isBlank() || lon.isBlank()) {
                    return
                }

                val favorite = Favorite(
                    name = name,
                    lat = lat,
                    lon = lon
                )
                viewModelScope.launch {
                    dao.upsertFavorite(favorite)
                }
                _state.update {
                    it.copy(
                        isAddingFavorite = false,
                        name = "",
                        lat = "",
                        lon = ""
                    )
                }
            }

            is FavoriteEvent.SetName -> {
                _state.update {
                    it.copy(
                        name = event.name
                    )
                }
            }

            is FavoriteEvent.SetLat -> {
                _state.update {
                    it.copy(
                        lat = event.lat
                    )
                }
            }

            is FavoriteEvent.SetLon -> {
                _state.update {
                    it.copy(
                        lon = event.lon
                    )
                }
            }

            is FavoriteEvent.ShowDialog -> {
                _state.update {
                    it.copy(
                        isAddingFavorite = true
                    )
                }
            }
        }
    }


}