package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import no.uio.ifi.in2000.rakettoppskytning.data.database.FavoriteDao
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherRepository
import no.uio.ifi.in2000.rakettoppskytning.model.historicalData.HistoricalPrecipitation
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Favorite
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteEvent
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteState
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherAtPos
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherAtPosHour
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.getVerticalSightKm
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.getVerticalSightKmNumber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import kotlin.math.roundToInt

data class WeatherUiState(
    val weatherAtPos: WeatherAtPos = WeatherAtPos()
)

data class HistoricalDataUIState(
    val historicalData: List<HistoricalPrecipitation> = listOf()
)

class HomeScreenViewModel(repo: WeatherRepository, private val dao: FavoriteDao) : ViewModel() {
    private val foreCastRep = repo
    private val gribRepo = foreCastRep.gribRepository
    val loading = mutableStateOf(false)
    val checkedGreen = mutableStateOf(true)
    val checkedRed = mutableStateOf(true)
    val sliderPosition = mutableStateOf(0f..360f)
    val options = (listOf("Lowest to highest", "Highest to lowest"))
    val isReversed = mutableStateOf(false)
    val text = mutableStateOf(options[0])
    val markedCardIndex = mutableIntStateOf(-1)
    val hasBeenFiltered = mutableStateOf(false)
    private val initialSelectedStartDateMillis = mutableStateOf(Calendar.getInstance())
    private val initialSelectedEndDateMillis = mutableStateOf(Calendar.getInstance())
    private val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    var startHour = mutableStateOf("")
    var endHour = mutableStateOf("")
    var startISOtime: String = ""
    var endISOtime: String = ""


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
    fun filterList() {
        hasBeenFiltered.value = true
        foreCastRep.resetFilter()
        var weatherAtPos: List<WeatherAtPosHour> = if (checkedGreen.value && !checkedRed.value) {
            weatherUiState.value.weatherAtPos.weatherList.filter { it.closeToLimitScore < 1 }

        } else if (!checkedGreen.value && checkedRed.value) {
            weatherUiState.value.weatherAtPos.weatherList.filter { it.closeToLimitScore >= 1 }

        } else {
            weatherUiState.value.weatherAtPos.weatherList
        }

        when (markedCardIndex.intValue) {
            0 -> {
                weatherAtPos = weatherAtPos.sortedBy { it.series.data.instant.details.windSpeed }
            }

            1 -> {
                weatherAtPos =
                    weatherAtPos.filter { it.series.data.instant.details.windFromDirection in sliderPosition.value.start..sliderPosition.value.endInclusive }
            }

            2 -> weatherAtPos =
                weatherAtPos.sortedBy { it.series.data.next1Hours?.details?.precipitationAmount }

            3 -> {
                weatherAtPos =
                    weatherAtPos.sortedBy {
                        it.series.data.instant.details.fogAreaFraction?.let { it1 ->
                            getVerticalSightKmNumber(
                                it1,
                                it.series.data.instant.details.cloudAreaFractionLow,
                                it.series.data.instant.details.cloudAreaFractionMedium,
                                it.series.data.instant.details.cloudAreaFractionHigh
                            )
                        }
                    }

            }

            4 -> {
                weatherAtPos =
                    weatherAtPos.sortedBy { it.series.data.instant.details.relativeHumidity }
            }

            5 -> {
                weatherAtPos =
                    weatherAtPos.sortedBy { it.series.data.instant.details.dewPointTemperature }
            }

            else -> {}
        }
        if (isReversed.value) {
            weatherAtPos = weatherAtPos.reversed()

        }
        weatherAtPos = weatherAtPos.filter { it.series.time in startISOtime..endISOtime }



        foreCastRep.updateWeatherAtPos(WeatherAtPos(weatherAtPos))

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getWeatherByCord(lat: Double, lon: Double) {
        Log.d("getWeather", "apicall")
        loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            foreCastRep.loadWeather(lat, lon)
            loading.value = false
            delay(100)
            filterList()

        }
    }

    val weatherUiState: StateFlow<WeatherUiState> =
        foreCastRep.observeWeather().map { WeatherUiState(weatherAtPos = it) }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WeatherUiState()
        )

    fun resetFilter() {
        checkedGreen.value = true
        checkedRed.value = true
        sliderPosition.value = 0f..360f
        isReversed.value = false
        text.value = options[0]
        markedCardIndex.intValue = -1


    }

    fun resetList() {
        foreCastRep.resetFilter()
    }


    init {
        initialSelectedStartDateMillis.value.time = Date()
        initialSelectedEndDateMillis.value.time = Date()
        initialSelectedEndDateMillis.value.add(Calendar.HOUR_OF_DAY, 24)
        startISOtime =
            sdf.format(initialSelectedStartDateMillis.value.timeInMillis)
                .replaceRange(14, 19, "00:00")

        endISOtime = sdf.format(initialSelectedEndDateMillis.value.timeInMillis)
        startHour.value = startISOtime.substring(11, 13)
        endHour.value = startISOtime.substring(11, 13)
        Log.d("starthour", "${startHour.value} ${endHour.value}")
        viewModelScope.launch {
            gribRepo.loadGribFiles()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    val dtrpState = mutableStateOf(
        DateRangePickerState(
            CalendarLocale("NO"),
            yearRange = 2024..2024,
            initialSelectedStartDateMillis = initialSelectedStartDateMillis.value.timeInMillis,
            initialSelectedEndDateMillis = initialSelectedEndDateMillis.value.timeInMillis,
        )
    )
    val validateHour = { x: Int -> if (x == 23) 0 else x }

    @OptIn(ExperimentalMaterial3Api::class)
    val tiState = mutableStateOf(
        TimePickerState(
            initialHour = validateHour(initialSelectedStartDateMillis.value.time.hours),
            0,
            true
        )
    )


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