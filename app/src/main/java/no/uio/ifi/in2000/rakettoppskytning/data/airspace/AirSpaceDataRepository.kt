package no.uio.ifi.in2000.rakettoppskytning.data.airspace

import AirSpace
import AirSpaceList
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AirSpaceDataRepository {
    private val _airSpaceRe = MutableStateFlow(AirSpaceList(listOf()))

    fun observeAirSpace(): StateFlow<AirSpaceList> = _airSpaceRe.asStateFlow()

    suspend fun loadAirSpace() {
        val space = getAirspace()

        _airSpaceRe.update { space }

    }
}