package no.uio.ifi.in2000.rakettoppskytning.ui.home.map

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.data.ballistic.simulateTrajectory
import no.uio.ifi.in2000.rakettoppskytning.model.grib.LevelData
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Favorite
import no.uio.ifi.in2000.rakettoppskytning.data.database.RocketSpecState

/** MapFactory implements the ViewModelProvider.Factory interface to generate instances of MapViewModel with given repositories.
 * This is used to provide a custom mechanism for creating instances of MapViewModel, allowing dependency injection of repositories into the view model
 *
 * */
class MapFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapViewModel() as T
    }
}

/**
 * MapViewModel handles map-related data and interactions.
 * It includes properties for latitude, longitude, trajectory simulation, camera options, and more.
 * Methods are provided for loading and deleting trajectory data, as well as updating the camera position on the map.
 * */
class MapViewModel : ViewModel() {

    private val initLat = 59.94363
    private val initLon = 10.71830

    private val _lat = mutableDoubleStateOf(initLat)
    private val _lon = mutableDoubleStateOf(initLon)
    private val _favorite = mutableStateOf(Favorite("", "", ""))

    val lat: MutableState<Double> = _lat
    val lon: MutableState<Double> = _lon
    val favorite = _favorite
    val makeTrajectory = mutableStateOf(false)
    val showTraDetails = mutableStateOf(false)
    val trajectory =
        mutableStateOf(listOf<no.uio.ifi.in2000.rakettoppskytning.model.trajectory.Point>())
    val threeD = mutableStateOf(true)

    fun loadTrajectory(allLevels: List<LevelData>, rocketSpecs: RocketSpecState) {
        viewModelScope.launch {
            if (trajectory.value.isEmpty()) {
                trajectory.value =
                    simulateTrajectory(
                        burnTime = rocketSpecs.burntime.toDouble(),
                        launchAngle = rocketSpecs.launchAngle.toDouble(),
                        launchDir = rocketSpecs.launchDirection.toDouble(),
                        altitude = 0.0,
                        thrust = rocketSpecs.thrust.toDouble(),
                        mass = rocketSpecs.dryWeight.toDouble(),
                        dt = 0.1,
                        allLevels = allLevels,
                        massDry = rocketSpecs.wetWeight.toDouble()
                    )
            }
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


