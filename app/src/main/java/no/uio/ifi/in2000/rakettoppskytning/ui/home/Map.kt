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
import com.mapbox.maps.extension.style.expressions.dsl.generated.get
import com.mapbox.maps.extension.style.expressions.dsl.generated.id
import com.mapbox.maps.extension.style.layers.generated.fillLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.generated.modelLayer
import com.mapbox.maps.extension.style.layers.properties.generated.ModelType
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor
import com.mapbox.maps.extension.style.model.model
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.removeOnMapClickListener
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.data.ballistic.simulateTrajectory
import no.uio.ifi.in2000.rakettoppskytning.model.grib.LevelData
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherAtPosHour
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.settings.SettingsViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.drawableToBitmap
import kotlin.math.atan2
import kotlin.math.roundToInt
import kotlin.math.sqrt


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
    mapViewModel: MapViewModel,
    settingsViewModel: SettingsViewModel
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
            Make3dtrajectory(mapViewModel, detailsScreenViewModel, settingsViewModel)

        } else if (settingsViewModel.ippcOnMap.value) {
            val LAYER_ID = "layer-id"
            val SOURCE_ID = "source-id"
            val TOP_LAYER_ID = "line-layer"
            val SETTLEMENT_LABEL = "settlement-major-label"
            val SOURCE_URL =
                "https://raw.githubusercontent.com/relet/pg-xc/master/geojson/luftrom.geojson"
            MapEffect() { mapView ->
                mapView.mapboxMap.apply {

                    loadStyle(
                        style(Style.OUTDOORS) {
                            +geoJsonSource(SOURCE_ID) {
                                data(SOURCE_URL)
                            }
                            +layerAtPosition(
                                fillLayer(LAYER_ID, SOURCE_ID) {
                                    fillColor(Color.parseColor("#0080ff")).fillOpacity(0.7)
                                },
                                below = SETTLEMENT_LABEL
                            )
                            +lineLayer(
                                TOP_LAYER_ID, SOURCE_ID
                            ) {

                                lineWidth(.5)
                            }
                        })
                }
            }
        } else {
            NewPointAnnotation(
                "",
                lat = lat,
                lon = lon,
                drawableId = R.drawable.pin,
                onClick = { Log.d("PointClick", it.point.toString()) }
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

@OptIn(MapboxExperimental::class)
@Composable
fun Make3dtrajectory(
    mapViewModel: MapViewModel,
    detailsScreenViewModel: DetailsScreenViewModel,
    settingsViewModel: SettingsViewModel,
) {
    val SOURCE_ID1 = "source1"
    val SAMPLE_MODEL_URI_1 = "asset://bigball.glb"
    val MODEL_ID_KEY = "model-id-key"
    val MODEL_ID_2 = "model-id-2"
    val SAMPLE_MODEL_URI_2 = "asset://portalrocketv3.glb"
    val cords = Point.fromLngLat(mapViewModel.lon.value, mapViewModel.lat.value)
    val weatherUiState by detailsScreenViewModel.weatherUiState.collectAsState()
    val favoriteUiState by detailsScreenViewModel.favoriteUiState.collectAsState()
    val time = detailsScreenViewModel.time.value
    var weatherAtPosHour: List<WeatherAtPosHour> = listOf()

    if (time.last() == 'f') {
        favoriteUiState.weatherAtPos.weatherList.forEach {
            if (it.date == time.dropLast(1)) {
                weatherAtPosHour = listOf(it)
            }
        }
    } else {
        weatherUiState.weatherAtPos.weatherList.forEach {
            if (it.date == time) {
                weatherAtPosHour = listOf(it)
            }

        }
    }
    val rocketSpecs = settingsViewModel.getRocketSpec()


    val allLevels: List<LevelData> =
        weatherAtPosHour.firstOrNull()?.verticalProfile?.getAllLevelDatas() ?: listOf()

    mapViewModel.loadTrajectory(allLevels, rocketSpecs)
    val tra = mapViewModel.trajectory.value

    val s = tra.find { it.z in 600.0..1000.0 }
    val hep = tra.indexOf(s)
    var pitch = Pair(0.0, 0.0)
    if (s != null) {
        pitch = calculatePitch(s, tra[hep + 20])
    }

    MapEffect { mapView ->
        mapView.mapboxMap.removeOnMapClickListener() {


            true
        }

        mapView.mapboxMap.apply {

            loadStyle(
                style(Style.OUTDOORS) {
                    tra.forEachIndexed { index, point ->
                        if (point.z < 0) {
                            return@forEachIndexed
                        } else if (index % settingsViewModel.sliderPosition.floatValue.roundToInt() == 0) {
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
                                val size = 2.0
                                modelScale(listOf(size, size, size))
                                modelTranslation(
                                    listOf(
                                        point.x,
                                        point.y * -1,
                                        point.z
                                    )
                                )
                                modelRotation(listOf(0.0, 0.0, 0.0))
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
                        modelScale(listOf(300.0, 300.0, 300.0))
                        if (s != null) {
                            modelTranslation(listOf(s.x, s.y * -1, s.z))
                        }



                        modelRotation(
                            listOf(
                                pitch.first * (rocketSpecs.launchAngle.toDouble() * yay(rocketSpecs.launchAngle.toDouble())) * -1,
                                pitch.second * (rocketSpecs.launchAngle.toDouble() * yay(rocketSpecs.launchAngle.toDouble())),
                                0.0
                            )
                        )


                        modelCastShadows(true)
                        modelReceiveShadows(true)
                        modelRoughness(0.1)
                    }

                }
            )

        }


    }
}


fun calculatePitch(
    start: no.uio.ifi.in2000.rakettoppskytning.data.ballistic.Point,
    end: no.uio.ifi.in2000.rakettoppskytning.data.ballistic.Point
): Pair<Double, Double> {
    val dx = end.x - start.x
    val dy = end.y - start.y
    val dz = end.z - start.z
    val distance = sqrt(dx * dx + dy * dy + dz * dz)
    val pitchX = atan2(dy, distance)
    val pitchY = atan2(-dx, dz)
    return Pair(pitchX, pitchY)
}

fun yay(number: Double): Double {
    if (number in 85.0..95.0) {
        return -.68
    } else if (number in 80.0..85.0) {
        return -.73
    } else if (number in 75.0..80.0) {
        return -.81
    } else if (number in 70.0..75.0) {
        return -.8
    } else if (number in 65.0..70.0) {
        return -.888
    } else if (number in 60.0..65.0) {
        return -.96
    } else if (number in 55.0..60.0) {
        return -1.36
    } else if (number in 50.0..55.0) {
        return -1.4
    } else if (number in 45.0..50.0) {
        return -1.8
    } else if (number in 40.0..45.0) {
        return -2.15
    } else if (number in 35.0..40.0) {
        return -2.73
    }


    return -0.0
}