package no.uio.ifi.in2000.rakettoppskytning.ui.favorites

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import no.uio.ifi.in2000.rakettoppskytning.data.favoriteCards.FavoriteCardRepository
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherRepository
import no.uio.ifi.in2000.rakettoppskytning.model.getCurrentDate
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
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WeatherUiState()
        )

    val favoriteUiState: StateFlow<FavoriteUiState> =
        favoriteRepo.observeFavorites().map { FavoriteUiState(favorites = it) }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FavoriteUiState()
        )

    fun getWeatherByFavorite(lat: Double, lon: Double, date: String) {
        Log.d("getWeather", "apicall")
        viewModelScope.launch(Dispatchers.IO) {
            repo.loadFavoriteCard(lat, lon, date)
        }
    }

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
        val expiredData = lastUpdated.value != getCurrentDate()
        if (isUpdatingWeatherData.value){
            Log.d("update", "Is loading. Update cancelled")
            return
        }

        if(expiredData && lastFavoritesUpdates.value.containsAll(favoriteUiState.value.favorites)){
            Log.d("update", "favorites contains the same favorites as last update. Update cancelled")
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