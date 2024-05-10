package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.util.Log
import androidx.annotation.MainThread
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import no.uio.ifi.in2000.rakettoppskytning.data.favoriteCards.FavoriteCardRepository
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherRepository
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Favorite
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherAtPos
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherAtPosHour
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherData
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.getVerticalSightKmNumber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

data class WeatherUiState(
    val weatherAtPos: WeatherData = WeatherAtPos()
)
data class FavoriteLocationUiState(
    val favorites: List<Favorite> = emptyList()
)

var isInitialized = mutableStateOf(false)

class HomeScreenViewModel(repo: WeatherRepository, val favoriteRepo: FavoriteCardRepository) : ViewModel() {
    private val weatherRepo = repo
    private val gribRepo = weatherRepo.gribRepository
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
    val getWeatherHasBeenCalled = mutableStateOf(false)

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
        weatherRepo.resetFilter()

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



        weatherRepo.updateWeatherAtPos(WeatherAtPos(weatherAtPos))

    }

    fun getWeatherByCord(lat: Double, lon: Double) {
        Log.d("getWeather", "apicall")
        loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepo.loadWeather(lat, lon)
            delay(100)
            filterList()
            loading.value = false
            getWeatherHasBeenCalled.value = true
        }
    }

    val weatherUiState: StateFlow<WeatherUiState> =
        weatherRepo.observeWeather()
            .map { WeatherUiState(weatherAtPos = it) }.stateIn(
                viewModelScope,
                started = SharingStarted.Eagerly,
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
        weatherRepo.resetFilter()
    }

    fun initialize() {
        if(isInitialized.value) return
        isInitialized.value = true
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
            initialDisplayMode = DisplayMode.Picker

        )
    )
    val favoriteUiState: StateFlow<FavoriteLocationUiState> =
        favoriteRepo.observeFavoriteLocations().map {
            FavoriteLocationUiState(
                favorites = it
            )
        }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FavoriteLocationUiState()
        )
    fun addFavorite(name: String, lat: String, lon: String) {
        viewModelScope.launch {
            favoriteRepo.insertFavoriteLocation(name, lat, lon)
        }
    }

    fun getFavoriteLocations() {
        try {
            viewModelScope.launch {
                favoriteRepo.getFavoriteLocation()
            }
        } catch (e: Exception) {
            Log.d("FavoriteLocation", e.stackTraceToString())
        }
    }

    fun deleteFavoriteLocation(name: String, lat: String, lon: String) {
        viewModelScope.launch {
            favoriteRepo.deleteFavoriteLocation(name, lat, lon)
        }
    }

}