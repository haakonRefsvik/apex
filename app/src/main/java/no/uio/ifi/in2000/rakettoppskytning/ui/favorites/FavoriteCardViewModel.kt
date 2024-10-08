package no.uio.ifi.in2000.rakettoppskytning.ui.favorites

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import no.uio.ifi.in2000.rakettoppskytning.data.formatting.getCurrentDate
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteCard
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherAtPosHour
import no.uio.ifi.in2000.rakettoppskytning.ui.home.WeatherUiState

/** FavoriteUiState is a data class representing the UI state for favorite weather data, containing a list of FavoriteCard objects.*/
data class FavoriteUiState(
    val favorites: List<FavoriteCard> = listOf()
)

/** FavoriteFactory implements the ViewModelProvider.Factory interface to generate instances of FavoriteCardViewModel with given repositories.
 * This is used to provide a custom mechanism for creating instances of FavoriteCardViewModel, allowing dependency injection of repositories into the view model
 *
 * */
class FavoriteFactory(
    private val repo: WeatherRepository,
    private val favoriteRepo: FavoriteCardRepository,
): ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoriteCardViewModel(repo, favoriteRepo) as T
    }
}


/**

 * FavoriteCardViewModel manages favorite weather data.
 * It interacts with WeatherRepository and FavoriteCardRepository where it fetches, deletes, adds and refreshes favoriteCards from database and
 * weather data. It also handles logic for updating and removing expired weather data
 *
 * */

class FavoriteCardViewModel(
    private val repo: WeatherRepository,
    private val favoriteRepo: FavoriteCardRepository
) : ViewModel() {
    private val lastUpdated = mutableStateOf("")
    private val lastFavoritesUpdates = mutableStateOf(listOf<FavoriteCard>())
    val isUpdatingWeatherData = mutableStateOf(false)
    val refreshKey = mutableIntStateOf(0)

    private val weatherDataUiState: StateFlow<WeatherUiState> =
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
        val hasData = (weatherDataUiState.value.weatherAtPos.weatherList.map { it.date })
            .containsAll(favoriteUiState.value.favorites.map { it.date })

        if (isUpdatingWeatherData.value){
            Log.d("favoriteCards", "wont reload, is already loading")
            viewModelScope.launch {
                isUpdatingWeatherData.value = true
                delay(dummyLoadDuration)
                isUpdatingWeatherData.value = false

            }
            return
        }

        if(!expiredData && noNewCards && hasData){
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

        Log.d("favoriteCards", "updating favcard-data")

        viewModelScope.launch {
            isUpdatingWeatherData.value = true
            repo.loadAllFavoriteCards(expiredData)
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