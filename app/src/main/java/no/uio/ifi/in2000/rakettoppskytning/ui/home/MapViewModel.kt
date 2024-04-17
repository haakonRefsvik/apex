package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.graphics.drawable.toDrawable
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.CameraState
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.style.expressions.dsl.generated.pitch
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.viewannotation.ViewAnnotationManager
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import kotlinx.coroutines.flow.asStateFlow
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Favorite
import kotlin.math.abs

class MapViewModel() : ViewModel() {

    private val initLat = 59.94363
    private val initLon = 10.71830

    private val _lat = mutableDoubleStateOf(initLat)
    private val _lon = mutableDoubleStateOf(initLon)
    private val _favorite = mutableStateOf(Favorite("", "", ""))

    val lat: MutableState<Double> = _lat
    val lon: MutableState<Double> = _lon
    val favorite = _favorite

    private val cam: CameraOptions = CameraOptions.Builder()
        .center(Point.fromLngLat(initLon, initLat))
        .zoom(10.0)
        .build()

    private val _cam = mutableStateOf(cam)

    val cameraOptions: MutableState<CameraOptions> = _cam

    fun updateCamera(lat: Double, lon: Double) {
        val newCameraState = CameraOptions.Builder()
            .center(Point.fromLngLat(lon, lat))
            .pitch(70.0)
            .build()
        _cam.value = newCameraState
    }

    @OptIn(MapboxExperimental::class)
    val mapViewportState: MapViewportState = MapViewportState()

    @OptIn(MapboxExperimental::class)
    fun moveMapCamera(lat: Double, lon: Double) {
        Log.d("moveMap 1: ", "$lat og $lon")

        val newCameraPosition = CameraOptions.Builder()
            .center(Point.fromLngLat(lon, lat))
            .build()

        val mapAnimationOptions = MapAnimationOptions.Builder()
            .duration(1000) // 1 sekund flyvetid
            .build()

        mapViewportState.flyTo(newCameraPosition, mapAnimationOptions)
        Log.d("moveMap 2: ", "$lat og $lon")

    }

}

