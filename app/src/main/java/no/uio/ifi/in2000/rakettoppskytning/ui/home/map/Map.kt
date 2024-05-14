package no.uio.ifi.in2000.rakettoppskytning.ui.home.map

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.res.ResourcesCompat
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.Style
import com.mapbox.maps.coroutine.styleDataLoadedEvents
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.settings.SettingsViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.drawableToBitmap


@OptIn(MapboxExperimental::class)
@Composable
fun NewPointAnnotation(
    text: String,
    lat: Double,
    lon: Double,
    drawableId: Int,
    onClick: (PointAnnotation) -> Unit,
    alt: Double = 0.0
) {

    PointAnnotation(
        point = Point.fromLngLat(lon, lat, alt),
        textField = text,
        textAnchor = TextAnchor.BOTTOM,
        textRadialOffset = 2.0,
        textColorInt = Color.RED,
        textEmissiveStrength = 20.0,
        iconSize = 0.04,
        iconImageBitmap = idToBitmap(id = drawableId),
        onClick = {
            onClick(it)
            true
        }
    )
}

@Composable
fun idToBitmap(id: Int): Bitmap {
    val context = LocalContext.current
    val myImage: Drawable = ResourcesCompat.getDrawable(context.resources, id, null)
        ?: throw Exception("Drawable $id not found")
    return drawableToBitmap(myImage)
}

@OptIn(MapboxExperimental::class)
@Composable
fun Map(
    detailsScreenViewModel: DetailsScreenViewModel,
    mapViewModel: MapViewModel,
    settingsViewModel: SettingsViewModel,
    homeScreenViewModel: HomeScreenViewModel
) {
    val lat by mapViewModel.lat
    val lon by mapViewModel.lon
    val cameraOptions by mapViewModel.cameraOptions
    mapViewModel.updateCamera(lat, lon)
    val mapViewportState = mapViewModel.mapViewportState
    mapViewportState.setCameraOptions(cameraOptions)

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewModel.mapViewportState,
    ) {


        if (mapViewModel.makeTrajectory.value) {
            Make3Dtrajectory(
                mapViewModel,
                detailsScreenViewModel,
                homeScreenViewModel,
                settingsViewModel
            )
        } else {
            NewPointAnnotation(
                "",
                lat = lat,
                lon = lon,
                drawableId = R.drawable.pin,
                onClick = { }
            )

            MapEffect { mapView ->
                mapView.mapboxMap.apply {

                    loadStyle(
                        style(Style.OUTDOORS) {}
                    )
                }
                mapView.mapboxMap.styleDataLoadedEvents

                mapView.mapboxMap.addOnMapClickListener {
                    mapViewModel.lat.value = it.latitude()
                    mapViewModel.lon.value = it.longitude()

                    false
                }

            }


        }

    }
}