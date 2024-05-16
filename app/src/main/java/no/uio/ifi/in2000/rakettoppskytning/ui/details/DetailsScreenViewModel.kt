package no.uio.ifi.in2000.rakettoppskytning.ui.details

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherRepository
import no.uio.ifi.in2000.rakettoppskytning.ui.home.WeatherUiState

/** DetailsFactory implements the ViewModelProvider.Factory interface to generate instances of DetailsScreenViewModel with given repositories.
 * This is used to provide a custom mechanism for creating instances of DetailsScreenViewModel, allowing dependency injection of repositories into the view model
 *
 * */
class DetailsFactory(
    private val repo: WeatherRepository,
): ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailsScreenViewModel(repo) as T
    }
}

/**
 * DetailsScreenViewModel manages the UI's interaction with weather data,
 * observing favorites and toggling their status through the provided WeatherRepository.
 * */
class DetailsScreenViewModel(private val repo: WeatherRepository) : ViewModel() {
    val time = mutableStateOf("")
    val favoriteUiState: StateFlow<WeatherUiState> =
        repo.observeFavorites().map { WeatherUiState(weatherAtPos = it) }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WeatherUiState()
        )

    fun toggleFavorite(lat: Double, lon: Double, date: String, value: Boolean) {
        repo.toggleFavorite(lat, lon, date, value)
    }
}