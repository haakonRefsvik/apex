package no.uio.ifi.in2000.rakettoppskytning.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import no.uio.ifi.in2000.rakettoppskytning.model.settings.ThresholdValues


class ThresholdRepository(){

    private val _thresholdValues = MutableStateFlow<List<ThresholdValues>>(listOf())
    fun observeThresholdValues(): StateFlow<List<ThresholdValues>> = _thresholdValues.asStateFlow()

    suspend fun loadThresholdValues() {
        //TODO: Laste inn thresholdverdier fra database
        _thresholdValues.update { listOf(dummyData) }
    }

}

val dummyData = ThresholdValues(
    maxPrecipitation = 0.0,
    maxHumidity = 5.0,
    maxWind = 7.4,
    maxShearWind = 17.2
)