package no.uio.ifi.in2000.rakettoppskytning.ui.settings

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.data.ThresholdRepository
import no.uio.ifi.in2000.rakettoppskytning.data.database.FavoriteDao
import no.uio.ifi.in2000.rakettoppskytning.data.database.ThresholdsDao
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Favorite
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteEvent
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteState
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.ThresholdState
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Thresholds
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.ThresholdsEvent


class ThresholdViewModel(repo: ThresholdRepository, private val thresholdsDao: ThresholdsDao) : ViewModel(){
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
    suspend fun saveThresholdValues(){

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

        Log.d("threshold3: ", map.toString())

        thresholdRepo.updateThresholdValues(map, thresholdsDao)
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

