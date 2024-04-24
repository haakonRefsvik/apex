package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.res.ResourcesCompat
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
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
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.data.ballistic.convertMetersToLatLon
import no.uio.ifi.in2000.rakettoppskytning.data.ballistic.simulateTrajectory
import no.uio.ifi.in2000.rakettoppskytning.model.grib.LevelData
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherAtPosHour
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.drawableToBitmap
import java.util.logging.Level


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
    detailsScreenViewModel: DetailsScreenViewModel,
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

        MapEffect { mapView ->
            mapView.mapboxMap.styleDataLoadedEvents

            mapView.mapboxMap.addOnMapClickListener {
                mapViewModel.moveMapCamera(it.latitude(), it.longitude())

                mapView.postDelayed(
                    {
                        // Lets the camera move before updating the pin
                        mapViewModel.lat.value = it.latitude()
                        mapViewModel.lon.value = it.longitude()
                    },
                    0
                )




                true
            }

        }
        if (mapViewModel.makeTra.value) {
            Make3dtrajectory(mapViewModel, detailsScreenViewModel)
        } else {
            MapEffect() { mapView ->
                mapView.mapboxMap.apply {

                    loadStyle(
                        style(Style.OUTDOORS) {}
                    )
                }
            }

        }

    }
}

@OptIn(MapboxExperimental::class)
@Composable
fun Make3dtrajectory(mapViewModel: MapViewModel, detailsScreenViewModel: DetailsScreenViewModel) {
    val SOURCE_ID1 = "source1"
    val SAMPLE_MODEL_URI_1 = "asset://bigball.glb"
    val MODEL_ID_KEY = "model-id-key"
    val MODEL_ID_2 = "model-id-2"
    val SAMPLE_MODEL_URI_2 = "asset://portalrocketv3.glb"
    val cords = Point.fromLngLat(mapViewModel.lon.value, mapViewModel.lat.value)
    val weatherUiState by detailsScreenViewModel.weatherUiState.collectAsState()
    val time = detailsScreenViewModel.time.value
    var weatherAtPosHour: List<WeatherAtPosHour> = listOf()

    weatherUiState.weatherAtPos.weatherList.forEach {
        if (it.date == time) {
            weatherAtPosHour = listOf(it)
        }

    }
    val allLevels: HashMap<Double, LevelData> =
        weatherAtPosHour.firstOrNull()?.verticalProfile?.getAllLevelDatas() ?: hashMapOf()

//    val levelDatas = hashMapOf<Double, LevelData>()
//    levelDatas[850.0] = LevelData(850.0)
    val launchDir = 60.0
    val launchAngle = 85.0
    val tra: List<no.uio.ifi.in2000.rakettoppskytning.data.ballistic.Point> =
        simulateTrajectory(
            burnTime = 12.0,
            launchAngle = launchAngle,
            launchDir = launchDir,
            altitude = 0.0,
            thrust = 5000.0,
            apogee = 3500.0,
            mass = 200.0,
            dt = 0.1,
            allLevels = allLevels,
            massDry = 100.0
        )

    MapEffect() { mapView ->

        mapView.mapboxMap.apply {

            loadStyle(
                style(Style.OUTDOORS) {
                    tra.forEachIndexed { index, point ->
                        if (point.z < 0) {
                            return@forEachIndexed
                        } else if (index % 1 == 0) {


                            val MODEL_ID_1 = "model-id${index}"
                            val SOURCE_ID = "source-id$${index}"
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
                                modelScale(listOf(1.0, 1.0, 1.0))
                                modelTranslation(
                                    listOf(
                                        point.x,
                                        point.y,
                                        point.z
                                    )
                                ) // Translation for Model 1
                                modelRotation(listOf(0.0, 0.0, 0.0))
                                modelCastShadows(false)
                                modelReceiveShadows(false)
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
                        val s = tra.find { it.z in 600.0..700.0 }
                        modelId(get(MODEL_ID_KEY))
                        modelType(ModelType.COMMON_3D)
                        modelScale(listOf(200.0, 200.0, 200.0))
                        if (s != null) {
                            modelTranslation(listOf(s.x, s.y, s.z))
                        } // Translation for Model 2
                        modelRotation(listOf(0.0, 0.0, launchDir))
                        modelCastShadows(true)
                        modelReceiveShadows(true)
                        modelRoughness(0.1)
                    }

                }
            )

        }


    }


}
