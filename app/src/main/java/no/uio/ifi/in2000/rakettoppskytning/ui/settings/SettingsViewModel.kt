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
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.ThresholdValues


class SettingsViewModel(repo: SettingsRepository, private val thresholdsDao: ThresholdsDao) : ViewModel(){
    private val settingsRepo = repo

    val thresholdMutableStates = ThresholdType.entries.map {
        mutableDoubleStateOf(settingsRepo.getThresholdValue(it))
    }

    val rocketSpecMutableStates = RocketSpecType.entries.map {
        mutableDoubleStateOf(settingsRepo.getRocketSpecValue(it))
    }

    /**
     * Takes the values from the mutableStates and saves them in the Repo
     * */
    suspend fun updateThresholdValues(event: (ThresholdsEvent) -> Unit){
        val updatedThresholdsMap = HashMap<String, Double>().apply {
            thresholdMutableStates.forEachIndexed { index, mutableState ->
                put(ThresholdType.entries[index].name, mutableState.doubleValue)
            }
        }

        settingsRepo.updateThresholdValues(
            updatedThresholdsMap,
            thresholdsDao
        )
        // Saves all the new values in the database
        updatedThresholdsMap.forEach{
            event(ThresholdsEvent.SetNedbor(it.value.toString()))
        }
    }

    suspend fun updateRocketSpecValues(){
        val updatedRocketSpecMap = HashMap<String, Double>().apply {
            rocketSpecMutableStates.forEachIndexed { index, mutableState ->
                put(RocketSpecType.entries[index].name, mutableState.doubleValue)
            }
        }

        settingsRepo.updateRocketSpecValues(
            updatedRocketSpecMap,
        )
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

