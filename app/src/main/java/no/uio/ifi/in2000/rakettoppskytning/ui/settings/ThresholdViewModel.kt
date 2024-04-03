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
import no.uio.ifi.in2000.rakettoppskytning.data.ThresholdType
import no.uio.ifi.in2000.rakettoppskytning.data.ThresholdValues


class ThresholdViewModel(repo: ThresholdRepository) : ViewModel(){
    private val thresholdRepo = repo
    private val map = thresholdRepo.getThresholdsMap()

    val maxPrecipitation = mutableDoubleStateOf(map[ThresholdType.MAX_PRECIPITATION.name] ?: 0.0)
    val maxHumidity = mutableDoubleStateOf(map[ThresholdType.MAX_HUMIDITY.name] ?: 0.0)
    val maxWind = mutableDoubleStateOf(map[ThresholdType.MAX_WIND.name] ?: 0.0)
    val maxShearWind = mutableDoubleStateOf(map[ThresholdType.MAX_SHEAR_WIND.name] ?: 0.0)
    val maxDewPoint = mutableDoubleStateOf(map[ThresholdType.MAX_DEW_POINT.name] ?: 0.0)

    val apogee = mutableDoubleStateOf(map[ThresholdType.MAX_DEW_POINT.name] ?: 0.0)

    /**
     * Takes the values from the mutableStates and saves them in the ThresholdRepository
     * */
    fun saveThresholdValues(){

        val maxPrecipitation: Double = maxPrecipitation.doubleValue
        val maxHumidity: Double = maxHumidity.doubleValue
        val maxWind: Double = maxWind.doubleValue
        val maxShearWind: Double = maxShearWind.doubleValue
        val minDewPoint: Double = maxDewPoint.doubleValue

        val map = hashMapOf<String, Double>()
        map[ThresholdType.MAX_PRECIPITATION.name] = maxPrecipitation
        map[ThresholdType.MAX_HUMIDITY.name] = maxHumidity
        map[ThresholdType.MAX_WIND.name] = maxWind
        map[ThresholdType.MAX_SHEAR_WIND.name] = maxShearWind
        map[ThresholdType.MAX_DEW_POINT.name] = minDewPoint

        thresholdRepo.updateThresholdValues(map)
    }

}
