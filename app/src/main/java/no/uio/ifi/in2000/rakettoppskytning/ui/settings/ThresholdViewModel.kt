package no.uio.ifi.in2000.rakettoppskytning.ui.settings

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.data.ThresholdRepository
import no.uio.ifi.in2000.rakettoppskytning.data.ThresholdValues


class ThresholdViewModel(repo: ThresholdRepository) : ViewModel(){
    private val thresholdRepo = repo
    private val map = thresholdRepo.getThresholdsMap()

    val maxPrecipitation: MutableState<Double> = mutableDoubleStateOf(map["maxPrecipitation"] ?: 0.0)
    val maxHumidity: MutableState<Double> = mutableDoubleStateOf(map["maxHumidity"] ?: 0.0)
    val maxWind: MutableState<Double> = mutableDoubleStateOf(map["maxWind"] ?: 0.0)
    val maxShearWind: MutableState<Double> = mutableDoubleStateOf(map["maxShearWind"] ?: 0.0)
    val maxDewPoint: MutableState<Double> = mutableDoubleStateOf(map["maxDewPoint"] ?: 0.0)

    /**
     * Takes the values from the mutableStates and saves them in the ThresholdRepository
     * */
    fun saveThresholdValues(){

        val maxPrecipitation: Double = maxPrecipitation.value
        val maxHumidity: Double = maxHumidity.value
        val maxWind: Double = maxWind.value
        val maxShearWind: Double = maxShearWind.value
        val minDewPoint: Double = maxDewPoint.value

        val map = hashMapOf<String, Double>()
        map["maxPrecipitation"] = maxPrecipitation
        map["maxHumidity"] = maxHumidity
        map["maxWind"] = maxWind
        map["maxShearWind"] = maxShearWind
        map["maxDewPoint"] = minDewPoint

        thresholdRepo.updateThresholdValues(map)
    }

}
