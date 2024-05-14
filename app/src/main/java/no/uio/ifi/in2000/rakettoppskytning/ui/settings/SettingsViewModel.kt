package no.uio.ifi.in2000.rakettoppskytning.ui.settings

import android.util.Log
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import no.uio.ifi.in2000.rakettoppskytning.data.settings.SettingsRepository
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.RocketSpecState
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.RocketSpecType
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.ThresholdType

class SettingsFactory(
    private val repo: SettingsRepository,
): ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(repo) as T
    }
}

// is set to true if there is any onValueChange
var settingsChangesMade = false

class SettingsViewModel(
    repo: SettingsRepository,
) :
    ViewModel() {
    private val settingsRepo = repo

    val weatherValueChosen = mutableStateOf(true)
    val rocketProfileChosen = mutableStateOf(false)

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
        )
    }

    fun getRocketSpec(): RocketSpecState {
        return RocketSpecState(
            apogee = rocketSpecMutableStates[RocketSpecType.APOGEE.ordinal].doubleValue.toString(),
            launchAngle = rocketSpecMutableStates[RocketSpecType.LAUNCH_ANGLE.ordinal].doubleValue.toString(),
            launchDirection = rocketSpecMutableStates[RocketSpecType.LAUNCH_DIRECTION.ordinal].doubleValue.toString(),
            thrust = rocketSpecMutableStates[RocketSpecType.THRUST_NEWTONS.ordinal].doubleValue.toString(),
            burntime = rocketSpecMutableStates[RocketSpecType.BURN_TIME.ordinal].doubleValue.toString(),
            dryWeight = rocketSpecMutableStates[RocketSpecType.DRY_WEIGHT.ordinal].doubleValue.toString(),
            wetWeight = rocketSpecMutableStates[RocketSpecType.WET_WEIGHT.ordinal].doubleValue.toString(),
            resolution = rocketSpecMutableStates[RocketSpecType.RESOLUTION.ordinal].doubleValue.toString(),
        )
    }

}

