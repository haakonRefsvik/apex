package no.uio.ifi.in2000.rakettoppskytning.ui.settings

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import no.uio.ifi.in2000.rakettoppskytning.data.ThresholdRepository
import no.uio.ifi.in2000.rakettoppskytning.model.settings.ThresholdValues
import kotlin.math.abs


data class ThresholdsUiState(
    val thresholdValues: List<ThresholdValues> = listOf()
)
class ThresholdViewModel(repo: ThresholdRepository) : ViewModel(){
    private val thresholdRepo = repo

    private val _maxPrecipitation = mutableDoubleStateOf(0.0)
    private val _maxHumidity = mutableDoubleStateOf(0.0)
    private val _maxWind = mutableDoubleStateOf(0.0)
    private val _maxShearWind = mutableDoubleStateOf(0.0)
    private val _minDewPoint = mutableDoubleStateOf(0.0)

    val maxPrecipitation: MutableState<Double> = _maxPrecipitation
    val maxHumidity: MutableState<Double> = _maxHumidity
    val maxWind: MutableState<Double> = _maxWind
    val maxShearWind: MutableState<Double> = _maxShearWind
    val minDewPoint: MutableState<Double> = _minDewPoint

    fun getCloseness(limit: Double, value: Double, max: Boolean = true): Double{
        if(max){
            if(value >= limit){
                return 1.0
            }
            val d = limit - value
            return 1 - (d / limit)
        }else{
            if(value <= limit){
                return 1.0
            }
            val d = value - limit
            return 1 - (d / limit)
        }
    }
    fun getValueStatusColor(aggregatedClosenessValues: Double): Color{

        if(aggregatedClosenessValues == 1.0){
            return Color.Red
        }

        if(aggregatedClosenessValues < 1 && aggregatedClosenessValues > 0.5){
            return Color.Yellow
        }

        return Color.Green
    }

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
