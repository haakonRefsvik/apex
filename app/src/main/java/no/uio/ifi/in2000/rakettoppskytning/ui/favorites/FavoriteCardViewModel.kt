package no.uio.ifi.in2000.rakettoppskytning.ui.favorites

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import no.uio.ifi.in2000.rakettoppskytning.data.favoriteCards.FavoriteCardRepository
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherRepository
import no.uio.ifi.in2000.rakettoppskytning.model.formatting.getCurrentDate
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteCard
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherAtPosHour
import no.uio.ifi.in2000.rakettoppskytning.ui.home.WeatherUiState

data class FavoriteUiState(
    val favorites: List<FavoriteCard> = listOf()
)

class FavoriteCardViewModel(val repo: WeatherRepository, val favoriteRepo: FavoriteCardRepository) : ViewModel() {
    val lastUpdated = mutableStateOf("")
    val lastFavoritesUpdates = mutableStateOf(listOf<FavoriteCard>())
    val isUpdatingWeatherData = mutableStateOf(false)
    val refreshKey = mutableIntStateOf(0)

    val weatherDataUiState: StateFlow<WeatherUiState> =
        repo.observeFavorites().map { WeatherUiState(weatherAtPos = it) }.stateIn(
            viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = WeatherUiState()
        )

    val favoriteUiState: StateFlow<FavoriteUiState> =
        favoriteRepo.observeFavorites().map { FavoriteUiState(favorites = it) }.stateIn(
            viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = FavoriteUiState()
        )

    suspend fun findNameByLatLon(lat: Double, lon: Double): String? {
        return withContext(Dispatchers.IO) {
            favoriteRepo.getFavoriteByLatLon(lat.toString(), lon.toString())
        }
    }

    fun getFavoriteWeatherData(lat: String, lon: String, date: String): WeatherAtPosHour? {
        return weatherDataUiState.value.weatherAtPos.weatherList.find {
            it.date == date && it.lon == lon.toDouble() && it.lat == lat.toDouble()
        }

    }
    fun addFavoriteCard(lat: String, lon: String, date: String){
        viewModelScope.launch {
            favoriteRepo.insertFavoriteCard(date, lat, lon)
        }
    }

    fun deleteFavoriteCard(lat: String, lon: String, date: String){
        repo.toggleFavorite(lat.toDouble(), lon.toDouble(), date, false)
        viewModelScope.launch {
            favoriteRepo.deleteFavoriteCard(date, lat, lon)
        }
    }

    fun refreshWeatherData() {
        val dummyLoadDuration = (600).toLong()
        val expiredData = (lastUpdated.value != getCurrentDate() && lastUpdated.value != "")
        val noNewCards = (lastFavoritesUpdates.value.isNotEmpty() && lastFavoritesUpdates.value.containsAll(favoriteUiState.value.favorites))


        if (isUpdatingWeatherData.value){
            Log.d("favoriteCards", "wont reload, is already loading")
            viewModelScope.launch {
                isUpdatingWeatherData.value = true
                delay(dummyLoadDuration)
                isUpdatingWeatherData.value = false

            }
            return
        }

        if(!expiredData && noNewCards){
            Log.d("favoriteCards", "wont reload, data is new")
            viewModelScope.launch {
                isUpdatingWeatherData.value = true
                delay(dummyLoadDuration)
                isUpdatingWeatherData.value = false
            }
            return
        }

        lastFavoritesUpdates.value = favoriteUiState.value.favorites
        lastUpdated.value = getCurrentDate()

        viewModelScope.launch {
            isUpdatingWeatherData.value = true
            repo.loadAllFavoriteCards(favoriteUiState.value.favorites, expiredData)
            isUpdatingWeatherData.value = false
            refreshKey.intValue++
        }

    }
    fun getFavoritesFromDatabase(){
        try {
            viewModelScope.launch {
                favoriteRepo.getFavoriteCards()
            }
        }catch (e: Exception){
            Log.d("FavoriteCards", e.stackTraceToString())
        }
    }

    fun removeExpiredCards(){
        viewModelScope.launch {
            favoriteRepo.removeExpiredCards(getCurrentDate(1))
        }
    }

}