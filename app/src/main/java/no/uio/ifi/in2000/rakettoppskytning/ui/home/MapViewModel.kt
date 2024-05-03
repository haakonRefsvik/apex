package no.uio.ifi.in2000.rakettoppskytning.ui.home

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import no.uio.ifi.in2000.rakettoppskytning.data.ballistic.simulateTrajectory
import no.uio.ifi.in2000.rakettoppskytning.model.grib.LevelData
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Favorite
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.RocketSpecState

class MapViewModel() : ViewModel() {

    companion object {
        const val INIT_LAT = 59.94363
        const val INIT_LON = 10.71830
        const val DEFAULT_ZOOM = 10.0
        const val MAXIMUM_PITCH = 70.0
        const val TRAJECTORY_UPDATE_THRESHOLD = 0.0001 // Adjust as needed based on actual use case
    }

    private var _lat by mutableStateOf(INIT_LAT)
    private var _lon by mutableStateOf(INIT_LON)
    private var _favorite by mutableStateOf(Favorite("", "", ""))
    private var _cameraOptions by mutableStateOf(createDefaultCameraOptions(_lat, _lon))
    private var _makeTrajectory by mutableStateOf(false)
    private var _trajectory = mutableStateOf(listOf<no.uio.ifi.in2000.rakettoppskytning.data.ballistic.Point>())

    val latitude get() = _lat
    val longitude get() = _lon
    val favorite get() = _favorite
    val makeTrajectory get() = _makeTrajectory
    val trajectory get() = _trajectory
    val cameraOptions get() = _cameraOptions
    /*
    private val initLat = 59.94363
    private val initLon = 10.71830

    private val _lat = mutableDoubleStateOf(initLat)
    private val _lon = mutableDoubleStateOf(initLon)
    private val _favorite = mutableStateOf(Favorite("", "", ""))

    val lat: MutableState<Double> = _lat
    val lon: MutableState<Double> = _lon
    val favorite = _favorite
    val makeTrajectory = mutableStateOf(false)
    val trajectory = mutableStateOf(listOf<no.uio.ifi.in2000.rakettoppskytning.data.ballistic.Point>())
*/
    fun loadTrajectory(allLevels: List<LevelData>, rocketSpecs: RocketSpecState){
        if(trajectory.value.isEmpty()){
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

    private fun createDefaultCameraOptions(lat: Double, lon: Double) = CameraOptions.Builder()
        .center(Point.fromLngLat(lon, lat))
        .zoom(DEFAULT_ZOOM)
        .build()

    fun deleteTrajectory(){
        trajectory.value = listOf()
        _makeTrajectory = false
    }

    @OptIn(MapboxExperimental::class)
    val mapViewportState: MapViewportState = MapViewportState()


    private val cam: CameraOptions = CameraOptions.Builder()
        .center(Point.fromLngLat(INIT_LON, INIT_LAT))
        .zoom(10.0)
        .build()

    private val _cam = mutableStateOf(cam)

    //val cameraOptions: MutableState<CameraOptions> = _cam

    fun updateCamera(lat: Double, lon: Double) {
        if (!makeTrajectory) {
            val newCameraState = CameraOptions.Builder()
                .center(Point.fromLngLat(lon, lat))
                .pitch(0.0)
                .build()
            _cam.value = newCameraState

        } else {
            val newCameraState = CameraOptions.Builder()
                .center(Point.fromLngLat(lon, lat))
                .pitch(MAXIMUM_PITCH)
                .zoom(12.0)
                .build()
            _cam.value = newCameraState

        }

    }


}


