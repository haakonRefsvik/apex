package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.ViewAnnotationAnchor
import com.mapbox.maps.coroutine.styleDataLoadedEvents
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.extension.style.expressions.dsl.generated.pitch
import com.mapbox.maps.extension.style.expressions.dsl.generated.zoom
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings
import com.mapbox.maps.viewannotation.annotationAnchors
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import no.uio.ifi.in2000.rakettoppskytning.R

@OptIn(MapboxExperimental::class)
@Composable
fun Map(
    homeScreenViewModel: HomeScreenViewModel,
){
    /*
    val lat by homeScreenViewModel.lat
    val lon by homeScreenViewModel.lon

    val mapBoxUiSettings: GesturesSettings by remember {
        mutableStateOf(GesturesSettings {
            rotateEnabled = false
            pinchToZoomEnabled = true
            pitchEnabled = true
        })
    }

    val mapViewportState = MapViewportState ()

    fun updateMapPosition(newLat: Double, newLon: Double) {
        mapViewportState.flyTo(
            cameraOptions {
                center(Point.fromLngLat(lat, lon))
            }
        )
    }

    MapboxMap(
        Modifier.fillMaxSize(),
        gesturesSettings = mapBoxUiSettings,
        mapViewportState = MapViewportState().apply {
            setCameraOptions {
                zoom(10.0)
                center(Point.fromLngLat(lon, lat))
                pitch(0.0)
            }
        }
    ) {
        var s by remember {
            mutableStateOf((viewAnnotationOptions {
                geometry(Point.fromLngLat(lon, lat))
                annotationAnchors(
                    {
                        anchor(ViewAnnotationAnchor.CENTER)
                    }
                )
                height(60.0)
                visible(false)
                allowOverlap(false)
            }))
        }

        MapEffect(Unit) { mapView ->
            mapView.mapboxMap.styleDataLoadedEvents

            mapView.mapboxMap.addOnMapClickListener {
                Log.d("s", "${it.latitude()},${it.longitude()}")
                homeScreenViewModel.lat.value = it.latitude()
                homeScreenViewModel.lon.value = it.longitude()

                s = viewAnnotationOptions {
                    geometry(Point.fromLngLat(lon, lat))
                    annotationAnchors(
                        {
                            anchor(ViewAnnotationAnchor.CENTER)
                        }
                    )
                    height(100.0)
                    visible(true)
                    allowOverlap(false)
                }

                true
            }

            // mapView.mapboxMap.addOnScaleListener (listener = )
        }

        ViewAnnotation(
            options = s
        ) {
            IconButton(onClick = { /*TODO*/ }) {
                Image(painterResource(id = R.drawable.rakkettpin), "RakketPin")
            }
        }
    }

     */
}

