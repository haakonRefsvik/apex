package no.uio.ifi.in2000.rakettoppskytning.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherForeCastLocationRepo
import no.uio.ifi.in2000.rakettoppskytning.ui.home.ForeCastUiState
import no.uio.ifi.in2000.rakettoppskytning.ui.home.VerticalProfileUiState

class DetailsScreenViewModel(repo: WeatherForeCastLocationRepo) : ViewModel() {

    val verticalProfileUiState: StateFlow<VerticalProfileUiState> =
        repo.observeVerticalProfiles().map { VerticalProfileUiState(verticalProfiles = it) }
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = VerticalProfileUiState()
            )

    val foreCastUiState: StateFlow<ForeCastUiState> =
        repo.observeForecast().map { ForeCastUiState(foreCast = it) }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ForeCastUiState()
        )
}