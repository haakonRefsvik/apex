package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import android.util.Log
import androidx.core.graphics.drawable.toDrawable
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.viewannotation.ViewAnnotationManager
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import kotlinx.coroutines.flow.asStateFlow
import no.uio.ifi.in2000.rakettoppskytning.R
import kotlin.math.abs

data class MapUiState(
    val isLoading : Boolean = false,
    val mapView: MapView? = null,
)
class MapViewModel() : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()
    private fun updateUiState(update: (MapUiState) -> MapUiState) {
        try {
            _uiState.value = update(_uiState.value)
        } catch (e: Exception) {
            Log.e("MapViewModel", "updateUiState: ${e.message}")
        }
    }

    private fun cameraToPos(lat: Double, lon: Double) {
        updateUiState { it.copy(isLoading = true) }

        if (_uiState.value.mapView != null) {
            val map = _uiState.value.mapView!!.mapboxMap
            val currentCameraPosition = map.cameraState
            val currentLat = currentCameraPosition.center.latitude()
            val currentLon = currentCameraPosition.center.longitude()
            val duration = (abs(currentLat - lat) + abs(currentLon - lon)).toLong()

            val cameraOptions = CameraOptions.Builder()
                .center(Point.fromLngLat(lon, lat))
                .build()

            val mapAnimationOptions = MapAnimationOptions.Builder()
                .duration(duration)
                .build()

            map.flyTo(cameraOptions, mapAnimationOptions)

        }
        updateUiState { it.copy(isLoading = false) }

    }

    /*  TODO

    private fun addViewAnnotation(
        point: Point,
        viewAnnotationManager: ViewAnnotationManager,
    ){

        val bitmap = convertDrawableToBitmap(R.drawable.rakkettpin.toDrawable())
        val viewAnnotation = viewAnnotationManager.addViewAnnotation(
            // Specify the layout resource id
            resId = R.layout.,
            // Set any view annotation options
            options = viewAnnotationOptions {
                // View annotation is placed at the specific geo coordinate
                geometry(point)
            }
        )

    }

     */


    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
// copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }
}
