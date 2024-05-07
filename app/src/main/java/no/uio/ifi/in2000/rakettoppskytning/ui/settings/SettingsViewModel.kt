package no.uio.ifi.in2000.rakettoppskytning.ui.settings

import android.util.Log
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import no.uio.ifi.in2000.rakettoppskytning.data.database.RocketSpecsDao
import no.uio.ifi.in2000.rakettoppskytning.data.settings.SettingsRepository
import no.uio.ifi.in2000.rakettoppskytning.data.database.ThresholdsDao
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.RocketSpecState
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.RocketSpecs
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.ThresholdState
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Thresholds
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.RocketSpecType
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.ThresholdType


class SettingsViewModel(
    repo: SettingsRepository,
    private val thresholdsDao: ThresholdsDao,
    private val rocketSpecsDao: RocketSpecsDao
) :
    ViewModel() {
    private val settingsRepo = repo

    val weatherValueChosen = mutableStateOf(true)
    val rocketProfileChosen = mutableStateOf(false)
    val ippcOnMap = mutableStateOf(true)

    val thresholdMutableStates = ThresholdType.entries.map {
        mutableDoubleStateOf(settingsRepo.getThresholdValue(it))
    }

    val rocketSpecMutableStates = RocketSpecType.entries.map {
        mutableDoubleStateOf(settingsRepo.getRocketSpecValue(it))
    }

    /**
     * Takes the values from the mutableStates and saves them in the Repo
     * */
    suspend fun updateThresholdValues() {
        val updatedThresholdsMap = HashMap<String, Double>().apply {
            try {
                thresholdMutableStates.forEachIndexed { index, mutableState ->
                    put(ThresholdType.entries[index].name, mutableState.doubleValue)
                }
            } catch (e: Exception) {
                Log.d("settings", "Could not update thresholds\n ${e.stackTrace}")
            }

        }

        settingsRepo.updateThresholdValues(
            updatedThresholdsMap,
            thresholdsDao
        )
    }

    suspend fun updateRocketSpecValues() {
        val updatedRocketSpecMap = HashMap<String, Double>().apply {
            rocketSpecMutableStates.forEachIndexed { index, mutableState ->
                put(RocketSpecType.entries[index].name, mutableState.doubleValue)
            }
        }

        settingsRepo.updateRocketSpecValues(
            updatedRocketSpecMap,
            rocketSpecsDao
        )
    }

    private val _thresholds: Flow<Thresholds?> = thresholdsDao.getThresholdById(1)

    private val _thresholdstate = MutableStateFlow(ThresholdState())

    val thresholdState = combine(_thresholdstate, _thresholds) { state, thresholds ->
        state.copy(
            percipitation = thresholds?.percipitation ?: "",
            humidity = thresholds?.humidity ?: "",
            wind = thresholds?.wind ?: "",
            shearWind = thresholds?.shearWind ?: "",
            dewpoint = thresholds?.dewpoint ?: ""
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThresholdState())

    private val _rocketspecs: Flow<RocketSpecs?> = rocketSpecsDao.getRocketSpecsById(1)

    private val _rocketspecsState = MutableStateFlow(RocketSpecState())

    val rocketspecsState = combine(_rocketspecsState, _rocketspecs) { state, rocketspecs ->
        state.copy(
            apogee = rocketspecs?.apogee ?: "",
            launchAngle = rocketspecs?.launchAngle ?: "",
            launchDirection = rocketspecs?.launchDirection ?: "",
            thrust = rocketspecs?.thrust ?: "",
            burntime = rocketspecs?.burntime ?: "",
            dryWeight = rocketspecs?.dryWeight ?: "",
            wetWeight = rocketspecs?.wetWeight ?: "",
            resolution = rocketspecs?.resolution ?: ""
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RocketSpecState())

    fun getRocketSpec(): RocketSpecState {
        return rocketspecsState.value
    }

}

