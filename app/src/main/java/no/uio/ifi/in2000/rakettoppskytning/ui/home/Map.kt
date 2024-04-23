package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.mapbox.maps.extension.style.layers.generated.modelLayer
import com.mapbox.maps.extension.style.expressions.dsl.generated.get
import com.mapbox.maps.extension.style.layers.properties.generated.ModelType
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor
import com.mapbox.maps.extension.style.model.model
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.data.ballistic.convertMetersToLatLon
import no.uio.ifi.in2000.rakettoppskytning.data.ballistic.simulateTrajectory
import no.uio.ifi.in2000.rakettoppskytning.model.grib.LevelData
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

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewModel.mapViewportState,
    ) {

        NewPointAnnotation(
            "",
            lat = lat,
            lon = lon,
            drawableId = R.drawable.pin,
            onClick = { Log.d("PointClick", it.point.toString()) }
        )

        MapEffect(Unit) { mapView ->
            mapView.mapboxMap.styleDataLoadedEvents

            mapView.mapboxMap.addOnMapClickListener {
                mapViewModel.moveMapCamera(it.latitude(), it.longitude())

                mapView.postDelayed(
                    {
                        // Lets the camera move before updating the pin
                        mapViewModel.lat.value = it.latitude()
                        mapViewModel.lon.value = it.longitude()
                    },
                    200
                )




                true
            }
        }
        Make3dtrajectory(Unit, mapViewModel)
    }
}

@OptIn(MapboxExperimental::class)
@Composable
fun Make3dtrajectory(unit: Unit, mapViewModel: MapViewModel) {
    val SOURCE_ID1 = "source1"
    val SAMPLE_MODEL_URI_1 = "asset://notball.glb"
    val MODEL_ID_KEY = "model-id-key"
    val MODEL_ID_2 = "model-id-2"
    val SAMPLE_MODEL_URI_2 = "asset://portalrocketV7.glb"
    val cords = Point.fromLngLat(mapViewModel.lon.value, mapViewModel.lat.value)


    val levelDatas = hashMapOf<Double, LevelData>()
    levelDatas[850.0] = LevelData(850.0)

    val tra: List<no.uio.ifi.in2000.rakettoppskytning.data.ballistic.Point> = simulateTrajectory(
        burnTime = 12.0,
        launchAngle = 80.0,
        launchDir = 0.0,
        altitude = 0.0,
        thrust = 4500.0,
        apogee = 3500.0,
        mass = 100.0,
        dt = 0.1,
        allLevels = levelDatas
    )


    MapEffect(unit) { mapView ->
        mapView.mapboxMap.apply {

            loadStyle(
                style(Style.OUTDOORS) {
                    tra.forEachIndexed { index, point ->
                        if (point.z < 0) {
                            return@forEachIndexed
                        } else if (index % 2 == 0) {


                            val MODEL_ID_1 = "model-id${index}"
                            val SOURCE_ID = "source-id$${index}"
                            val points = convertMetersToLatLon(
                                point.x,
                                point.y,
                                Pair(mapViewModel.lon.value, mapViewModel.lat.value)
                            )
                            val MODEL1_COORDINATES = Point.fromLngLat(
                                mapViewModel.lon.value, mapViewModel.lat.value
                            )
                            +model(MODEL_ID_1) {
                                uri(SAMPLE_MODEL_URI_1)
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

                                            )
                                    )
                                )
                            }
                            +modelLayer(MODEL_ID_1, SOURCE_ID) {
                                modelId(get(MODEL_ID_KEY))
                                modelType(ModelType.LOCATION_INDICATOR)
                                modelScale(listOf(8.0, 8.0, 8.0))
                                modelTranslation(
                                    listOf(
                                        point.x,
                                        point.y,
                                        point.z
                                    )
                                ) // Translation for Model 1
                                modelRotation(listOf(0.0, 0.0, 90.0))
                                modelCastShadows(true)
                                modelReceiveShadows(true)
                                modelRoughness(0.1)
                            }
                        }


                    }

                    +model(MODEL_ID_2) {
                        uri(SAMPLE_MODEL_URI_2)
                    }


                    +geoJsonSource(SOURCE_ID1) {
                        featureCollection(
                            FeatureCollection.fromFeatures(

                                listOf(

                                    Feature.fromGeometry(cords)
                                        .also {
                                            it.addStringProperty(
                                                MODEL_ID_KEY,
                                                MODEL_ID_2,
                                            )
                                        },

                                    )
                            )
                        )
                    }




                    +modelLayer(MODEL_ID_2, SOURCE_ID1) {
                        modelId(get(MODEL_ID_KEY))
                        modelType(ModelType.COMMON_3D)
                        modelScale(listOf(10.0, 10.0, 10.0))
                        modelTranslation(listOf(0.0, 0.0, 0.0)) // Translation for Model 2
                        modelRotation(listOf(0.0, 0.0, 180.0))
                        modelCastShadows(true)
                        modelReceiveShadows(true)
                        modelRoughness(0.1)
                    }

                }
            )

        }

    }


}
