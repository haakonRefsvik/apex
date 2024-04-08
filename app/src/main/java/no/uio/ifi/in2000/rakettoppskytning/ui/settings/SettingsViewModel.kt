package no.uio.ifi.in2000.rakettoppskytning.ui.settings

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.data.settings.SettingsRepository
import no.uio.ifi.in2000.rakettoppskytning.data.database.ThresholdsDao
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.ThresholdState
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Thresholds
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.ThresholdsEvent
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.RocketSpecType
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.ThresholdType


class SettingsViewModel(repo: SettingsRepository, private val thresholdsDao: ThresholdsDao) : ViewModel(){
    private val settingsRepo = repo
    private val map = settingsRepo.getThresholdsMap()

    val maxPrecipitation: MutableState<Double> = mutableDoubleStateOf(settingsRepo.getThresholdValue(ThresholdType.MAX_PRECIPITATION))
    val maxHumidity: MutableState<Double> = mutableDoubleStateOf(settingsRepo.getThresholdValue(ThresholdType.MAX_HUMIDITY))
    val maxWind: MutableState<Double> = mutableDoubleStateOf(settingsRepo.getThresholdValue(ThresholdType.MAX_WIND))
    val maxShearWind: MutableState<Double> = mutableDoubleStateOf(settingsRepo.getThresholdValue(ThresholdType.MAX_SHEAR_WIND))
    val maxDewPoint: MutableState<Double> = mutableDoubleStateOf(settingsRepo.getThresholdValue(ThresholdType.MAX_DEW_POINT))

    val apogee: MutableState<Double> = mutableDoubleStateOf(settingsRepo.getRocketSpecValue(RocketSpecType.APOGEE))
    val launchAngle: MutableState<Double> = mutableDoubleStateOf(settingsRepo.getRocketSpecValue(RocketSpecType.LAUNCH_ANGLE))
    val launchDirection: MutableState<Double> = mutableDoubleStateOf(settingsRepo.getRocketSpecValue(RocketSpecType.LAUNCH_DIRECTION))
    val thrust: MutableState<Double> = mutableDoubleStateOf(settingsRepo.getRocketSpecValue(RocketSpecType.THRUST_NEWTONS))

    /**
     * Takes the values from the mutableStates and saves them in the ThresholdRepository
     * */
    suspend fun saveThresholdValues(event: (ThresholdsEvent) -> Unit){
        val maxPrecipitation: Double = maxPrecipitation.value
        val maxHumidity: Double = maxHumidity.value
        val maxWind: Double = maxWind.value
        val maxShearWind: Double = maxShearWind.value
        val minDewPoint: Double = maxDewPoint.value

        val map = hashMapOf<String, Double>()
        map[ThresholdType.MAX_PRECIPITATION.name] = maxPrecipitation
        map[ThresholdType.MAX_HUMIDITY.name] = maxHumidity
        map[ThresholdType.MAX_WIND.name] = maxWind
        map[ThresholdType.MAX_SHEAR_WIND.name] = maxShearWind
        map[ThresholdType.MAX_DEW_POINT.name] = minDewPoint

        settingsRepo.updateThresholdValues(map, thresholdsDao)

        // Saves all the new values in the database
        map.forEach{
            event(ThresholdsEvent.SetNedbor(it.value.toString()))
        }

    }

    private val _thresholds: Flow<Thresholds?> = thresholdsDao.getThresholdById(1)

    private val _state = MutableStateFlow(ThresholdState())

    val state = combine(_state, _thresholds) { state, thresholds ->
        state.copy(
            nedbor = thresholds?.nedbor ?: "",
            luftfuktighet = thresholds?.luftfuktighet ?: "",
            vind = thresholds?.vind ?: "",
            shearWind = thresholds?.shearWind ?: "",
            duggpunkt = thresholds?.duggpunkt ?: ""
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThresholdState())

    fun onEvent(event: ThresholdsEvent) {
        when(event) {
            is ThresholdsEvent.SaveThreshold -> {
                val nedbor = state.value.nedbor
                val luftfuktighet = state.value.luftfuktighet
                val vind = state.value.vind
                val shearWind = state.value.shearWind
                val duggpunkt = state.value.duggpunkt

                if(nedbor.isBlank() || luftfuktighet.isBlank() || vind.isBlank()|| shearWind.isBlank() || duggpunkt.isBlank()) {
                    return
                }

                val thresholds = Thresholds(
                    nedbor = nedbor,
                    luftfuktighet = luftfuktighet,
                    vind = vind,
                    shearWind = shearWind,
                    duggpunkt = duggpunkt
                )
                viewModelScope.launch {
                    thresholdsDao.updateThreshold(thresholds)
                }

            }
            is ThresholdsEvent.SetNedbor -> {
                _state.update { it.copy(
                    nedbor = event.nedbor
                ) }
            }
            is ThresholdsEvent.SetLuftfuktighet -> {
                _state.update { it.copy(
                    luftfuktighet = event.luftfuktighet
                ) }
            }
            is ThresholdsEvent.SetVind -> {
                _state.update { it.copy(
                    vind = event.vind
                ) }
            }
            is ThresholdsEvent.SetShearWind -> {
                _state.update { it.copy(
                    shearWind = event.shearWind
                ) }
            }
            is ThresholdsEvent.SetDuggpunkt -> {
                _state.update { it.copy(
                    duggpunkt = event.duggpunkt
                ) }
            }
        }
    }
}

