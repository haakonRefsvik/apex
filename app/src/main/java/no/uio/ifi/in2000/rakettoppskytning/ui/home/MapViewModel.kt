package no.uio.ifi.in2000.rakettoppskytning.ui.home

import AirSpaceList
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.data.airspace.AirSpaceDataRepository
import no.uio.ifi.in2000.rakettoppskytning.data.ballistic.simulateTrajectory
import no.uio.ifi.in2000.rakettoppskytning.model.grib.LevelData
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Favorite
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.RocketSpecState
import no.uio.ifi.in2000.rakettoppskytning.ui.favorites.FavoriteUiState
import java.util.logging.Level

class MapViewModel() : ViewModel() {

    private val initLat = 59.94363
    private val initLon = 10.71830

    private val _lat = mutableDoubleStateOf(initLat)
    private val _lon = mutableDoubleStateOf(initLon)
    private val _favorite = mutableStateOf(Favorite("", "", ""))
    private val _airSpaceRepo = AirSpaceDataRepository()

    val lat: MutableState<Double> = _lat
    val lon: MutableState<Double> = _lon
    val favorite = _favorite
    val makeTrajectory = mutableStateOf(false)
    val showTraDetails = mutableStateOf(false)
    val trajectory =
        mutableStateOf(listOf<no.uio.ifi.in2000.rakettoppskytning.data.ballistic.Point>())
    val threeD = mutableStateOf(true)

    val airSpaceUiState: StateFlow<AirSpaceList> =
        _airSpaceRepo.observeAirSpace().map { it }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AirSpaceList(listOf())
        )

    fun getAirspace() {
        viewModelScope.launch(Dispatchers.IO) { _airSpaceRepo.loadAirSpace() }
    }

    fun loadTrajectory(allLevels: List<LevelData>, rocketSpecs: RocketSpecState) {
        if (trajectory.value.isEmpty()) {
            trajectory.value =
                simulateTrajectory(
                    burnTime = rocketSpecs.burntime.toDouble(),
                    launchAngle = rocketSpecs.launchAngle.toDouble(),
                    launchDir = rocketSpecs.launchDirection.toDouble(),
                    altitude = 0.0,
                    thrust = rocketSpecs.thrust.toDouble(),
                    apogee = rocketSpecs.apogee.toDouble(),
                    mass = rocketSpecs.dryWeight.toDouble(),
                    dt = 0.1,
                    allLevels = allLevels,
                    massDry = rocketSpecs.wetWeight.toDouble()
                )
        }
    }

    fun deleteTrajectory() {
        trajectory.value = listOf()
        makeTrajectory.value = false
    }

    @OptIn(MapboxExperimental::class)
    val mapViewportState: MapViewportState = MapViewportState()


    private val cam: CameraOptions = CameraOptions.Builder()
        .center(Point.fromLngLat(initLon, initLat))
        .zoom(10.0)
        .build()

    private val _cam = mutableStateOf(cam)

    val cameraOptions: MutableState<CameraOptions> = _cam

    fun updateCamera(lat: Double, lon: Double) {
        if (!makeTrajectory.value || !threeD.value) {
            val newCameraState = CameraOptions.Builder()
                .center(Point.fromLngLat(lon, lat))
                .pitch(0.0)
                .build()
            _cam.value = newCameraState

        } else {
            val newCameraState = CameraOptions.Builder()
                .center(Point.fromLngLat(lon, lat))
                .pitch(70.0)
                .zoom(12.0)
                .build()
            _cam.value = newCameraState

        }

    }


}


