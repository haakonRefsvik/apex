package no.uio.ifi.in2000.rakettoppskytning.ui.settings

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.data.database.RocketSpecsDao
import no.uio.ifi.in2000.rakettoppskytning.data.settings.SettingsRepository
import no.uio.ifi.in2000.rakettoppskytning.data.database.ThresholdsDao
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.RocketSpecState
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.RocketSpecs
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.RocketSpecsEvent
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.ThresholdState
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Thresholds
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.ThresholdsEvent
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.RocketSpecType
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.ThresholdType


class SettingsViewModel(
    repo: SettingsRepository,
    private val thresholdsDao: ThresholdsDao,
    private val rocketSpecsDao: RocketSpecsDao
) :
    ViewModel() {
    private val settingsRepo = repo

    val settingscheck1 = mutableStateOf(true)
    val settingscheck2 = mutableStateOf(false)

    val thresholdMutableStates = ThresholdType.entries.map {
        mutableDoubleStateOf(settingsRepo.getThresholdValue(it))
    }

    val rocketSpecMutableStates = RocketSpecType.entries.map {
        mutableDoubleStateOf(settingsRepo.getRocketSpecValue(it))
    }

    /**
     * Takes the values from the mutableStates and saves them in the Repo
     * */
    suspend fun updateThresholdValues(event: (ThresholdsEvent) -> Unit) {
        val updatedThresholdsMap = HashMap<String, Double>().apply {
            try {
                thresholdMutableStates.forEachIndexed { index, mutableState ->
                    put(ThresholdType.entries[index].name, mutableState.doubleValue)
                }
            }catch (e: Exception){
                Log.d("settings", "Could not update thresholds\n ${e.stackTrace}")
            }

        }

        settingsRepo.updateThresholdValues(
            updatedThresholdsMap,
            thresholdsDao
        )
        // Saves all the new values in the database
        updatedThresholdsMap.forEach {
            event(ThresholdsEvent.SetPercipitation(it.value.toString()))
        }
    }

    suspend fun updateRocketSpecValues(event: (RocketSpecsEvent) -> Unit) {
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

    fun onThresholdsEvent(event: ThresholdsEvent) {
        when (event) {
            is ThresholdsEvent.SaveThreshold -> {
                val percipitation = thresholdState.value.percipitation
                val humidity = thresholdState.value.humidity
                val wind = thresholdState.value.wind
                val shearWind = thresholdState.value.shearWind
                val dewpoint = thresholdState.value.dewpoint

                if (percipitation.isBlank() || humidity.isBlank() || wind.isBlank() || shearWind.isBlank() || dewpoint.isBlank()) {
                    return
                }

                val thresholds = Thresholds(
                    percipitation = percipitation,
                    humidity = humidity,
                    wind = wind,
                    shearWind = shearWind,
                    dewpoint = dewpoint
                )
                viewModelScope.launch {
                    thresholdsDao.updateThreshold(thresholds)
                }

            }

            is ThresholdsEvent.SetPercipitation -> {
                _thresholdstate.update {
                    it.copy(
                        percipitation = event.percipitation
                    )
                }
            }

            is ThresholdsEvent.SetHumidity -> {
                _thresholdstate.update {
                    it.copy(
                        humidity = event.humidity
                    )
                }
            }

            is ThresholdsEvent.SetWind -> {
                _thresholdstate.update {
                    it.copy(
                        wind = event.wind
                    )
                }
            }

            is ThresholdsEvent.SetShearWind -> {
                _thresholdstate.update {
                    it.copy(
                        shearWind = event.shearWind
                    )
                }
            }

            is ThresholdsEvent.SetDewpoint -> {
                _thresholdstate.update {
                    it.copy(
                        dewpoint = event.dewpoint
                    )
                }
            }
        }
    }

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
            dropTime = rocketspecs?.dropTime ?: ""
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RocketSpecState())

    fun getRocketSpec(): RocketSpecState {
        return rocketspecsState.value
    }

    fun onRocketSpecsEvent(event: RocketSpecsEvent) {
        when (event) {
            is RocketSpecsEvent.SaveRocketSpecs -> {
                val apogee = rocketspecsState.value.apogee
                val launcAngle = rocketspecsState.value.launchAngle
                val launchDirection = rocketspecsState.value.launchDirection
                val thrust = rocketspecsState.value.thrust
                val burntime = rocketspecsState.value.burntime
                val dryWeight = rocketspecsState.value.dryWeight
                val wetWeight = rocketspecsState.value.wetWeight
                val dropTime = rocketspecsState.value.dropTime

                if (apogee.isBlank() || launcAngle.isBlank() || launchDirection.isBlank() || thrust.isBlank() || burntime.isBlank() || dropTime.isBlank() || dryWeight.isBlank() || wetWeight.isBlank()) {
                    return
                }

                val rocketSpecs = RocketSpecs(
                    apogee = apogee,
                    launchAngle = launcAngle,
                    launchDirection = launchDirection,
                    thrust = thrust,
                    burntime = burntime,
                    dryWeight = dryWeight,
                    wetWeight = dropTime,
                    dropTime = thrust,
                )
                viewModelScope.launch {
                    rocketSpecsDao.updateRocketSpecs(rocketSpecs)
                }

            }

            is RocketSpecsEvent.SetApogee -> {
                _rocketspecsState.update {
                    it.copy(
                        apogee = event.apogee
                    )
                }
            }

            is RocketSpecsEvent.SetLauncAngle -> {
                _rocketspecsState.update {
                    it.copy(
                        launchAngle = event.launchAngle
                    )
                }
            }

            is RocketSpecsEvent.SetLaunchDirection -> {
                _rocketspecsState.update {
                    it.copy(
                        launchDirection = event.launchDirection
                    )
                }
            }

            is RocketSpecsEvent.SetThrust -> {
                _rocketspecsState.update {
                    it.copy(
                        thrust = event.thrust
                    )
                }
            }

            is RocketSpecsEvent.SetBurntime -> {
                _rocketspecsState.update {
                    it.copy(
                        burntime = event.burntime
                    )
                }
            }

            is RocketSpecsEvent.SetDryWeight -> {
                _rocketspecsState.update {
                    it.copy(
                        dryWeight = event.dryWeight
                    )
                }
            }

            is RocketSpecsEvent.SetWetWeight -> {
                _rocketspecsState.update {
                    it.copy(
                        wetWeight = event.wetWeight
                    )
                }
            }

            is RocketSpecsEvent.SetDropTime -> {
                _rocketspecsState.update {
                    it.copy(
                        dropTime = event.dropTime
                    )
                }
            }
        }
    }
}

