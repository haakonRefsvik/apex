package no.uio.ifi.in2000.rakettoppskytning.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherRepository
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HistoricalDataUIState
import no.uio.ifi.in2000.rakettoppskytning.ui.home.WeatherUiState

class DetailsScreenViewModel(repo: WeatherRepository) : ViewModel() {

    val weatherUiState: StateFlow<WeatherUiState> =
        repo.observeWeather().map { WeatherUiState(weatherAtPos = it) }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WeatherUiState()
        )

}