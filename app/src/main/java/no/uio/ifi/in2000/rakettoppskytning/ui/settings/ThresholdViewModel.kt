package no.uio.ifi.in2000.rakettoppskytning.ui.settings

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.data.ThresholdRepository
import no.uio.ifi.in2000.rakettoppskytning.model.settings.ThresholdValues
import no.uio.ifi.in2000.rakettoppskytning.ui.home.ForeCastUiState


data class ThresholdsUiState(
    val thresholdValues: List<ThresholdValues> = listOf()
)
class ThresholdViewModel(repo: ThresholdRepository) : ViewModel(){
    private val thresholdRepo = repo

    private val _maxPrecipitation = mutableDoubleStateOf(0.0)
    private val _maxHumidity = mutableDoubleStateOf(0.0)
    private val _maxWind = mutableDoubleStateOf(0.0)
    private val _maxShearWind = mutableDoubleStateOf(0.0)
    private val _maxDewPoint = mutableDoubleStateOf(0.0)

    val maxPrecipitation: MutableState<Double> = _maxPrecipitation
    val maxHumidity: MutableState<Double> = _maxHumidity
    val maxWind: MutableState<Double> = _maxWind
    val maxShearWind: MutableState<Double> = _maxShearWind
    val maxDewPoint: MutableState<Double> = _maxDewPoint

    /*
    val thresholdsUiState: StateFlow<ThresholdsUiState> =
        thresholdRepo.observeThresholdValues().map { ThresholdsUiState(thresholdValues = it) }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ThresholdsUiState()
        )

    init {
        viewModelScope.launch {
            thresholdRepo.loadThresholdValues()
        }
    }

     */


}
