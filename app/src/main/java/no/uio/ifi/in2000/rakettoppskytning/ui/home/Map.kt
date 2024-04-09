package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.res.ResourcesCompat
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.Style
import com.mapbox.maps.coroutine.styleDataLoadedEvents
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.style.expressions.dsl.generated.get
import com.mapbox.maps.extension.style.layers.generated.modelLayer
import com.mapbox.maps.extension.style.layers.properties.generated.ModelType
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor
import com.mapbox.maps.extension.style.model.model
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.drawableToBitmap


@OptIn(MapboxExperimental::class)
@Composable
fun NewPointAnnotation(
    text: String,
    lat: Double,
    lon: Double,
    drawableId: Int,
    onClick: (PointAnnotation) -> Unit
) {
    PointAnnotation(
        point = Point.fromLngLat(lon, lat),
        textField = text,
        textAnchor = TextAnchor.BOTTOM,
        textRadialOffset = 2.0,
        textColorInt = Color.RED,
        textEmissiveStrength = 20.0,
        iconSize = 0.06,
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
fun NewViewAnnotation(
    lat: Double,
    lon: Double,
) {
    val context = LocalContext.current
    ViewAnnotation(
        options = viewAnnotationOptions {
            geometry(Point.fromLngLat(lon, lat))
            allowOverlap(false)
        }
    ) {
        Card {
            Text(text = "ViewAnnotation")
        }
    }
}

@OptIn(MapboxExperimental::class)
@Composable
fun Map(
    homeScreenViewModel: HomeScreenViewModel,
    mapViewModel: MapViewModel
) {
    val lat by mapViewModel.lat
    val lon by mapViewModel.lon
    val cameraOptions by mapViewModel.cameraOptions
    mapViewModel.updateCamera(lat, lon)
    val mapViewportState = mapViewModel.mapViewportState
    mapViewportState.setCameraOptions(cameraOptions)
    var p by remember {
        mutableStateOf(viewAnnotationOptions {
            geometry(
                Point.fromLngLat(
                    lon,
                    lat
                )
            )
        })
    }

    val CAMERA_ZOOM = 16.0
    val CAMERA_PITCH = 45.0
    val SOURCE_ID = "source-id"
    val MODEL_LAYER_ID = "model-layer-id"
    val MODEL_ID_KEY = "model-id-key"
    val MODEL_ID_1 = "model-id-1"
    val MODEL_ID_2 = "model-id-2"
    val SAMPLE_MODEL_URI_1 =
        "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Duck/glTF-Embedded/Duck.gltf"
    val SAMPLE_MODEL_URI_2 = "asset://adamHead.gltf"
    val MAPBOX_HELSINKI = Point.fromLngLat(10.71830, 59.94363)
    val MODEL1_COORDINATES = Point.fromLngLat(
        MAPBOX_HELSINKI.longitude() - 0.002,
        MAPBOX_HELSINKI.latitude() + 0.002
    )

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewModel.mapViewportState,
    ) {

        NewPointAnnotation(
            "",
            lat = lat,
            lon = lon,
            drawableId = R.drawable.rakkettpin,
            onClick = { Log.d("PointClick", it.point.toString()) }
        )


        MapEffect(Unit) { mapView ->
            mapView.mapboxMap.styleDataLoadedEvents

            mapView.mapboxMap.addOnMapClickListener {
                Log.d("s", "${it.latitude()},${it.longitude()}")
                mapViewModel.lat.value = it.latitude()
                mapViewModel.lon.value = it.longitude()
                true
            }
            mapView.mapboxMap.apply {
                loadStyle(
                    style(Style.STANDARD) {
//                        +model(MODEL_ID_1) {
//                            uri(SAMPLE_MODEL_URI_1)
//                        }
                        +model(MODEL_ID_2) {
                            uri(SAMPLE_MODEL_URI_2)
                        }
                        +geoJsonSource(SOURCE_ID) {
                            featureCollection(
                                FeatureCollection.fromFeatures(
                                    listOf(
                                        Feature.fromGeometry(MODEL1_COORDINATES)
                                            .also {
                                                it.addStringProperty(
                                                    MODEL_ID_KEY,
                                                    MODEL_ID_1
                                                )
                                            },
                                        Feature.fromGeometry(MAPBOX_HELSINKI)
                                            .also { it.addStringProperty(MODEL_ID_KEY, MODEL_ID_2) }
                                    )
                                )
                            )
                        }
                        +modelLayer(MODEL_LAYER_ID, SOURCE_ID) {
                            modelId(get(MODEL_ID_KEY))
                            modelType(ModelType.COMMON_3D)
                            modelScale(listOf(2240.0, 2240.0, 2240.0))
                            modelTranslation(listOf(0.0, 0.0, 0.0))
                            modelRotation(listOf(0.0, 0.0, 90.0))

                            modelAmbientOcclusionIntensity(1.0)
                        }
                    }
                )
            }


        }
    }
}
