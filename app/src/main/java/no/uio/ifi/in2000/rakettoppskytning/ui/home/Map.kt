package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize

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
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolygonAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.mapbox.maps.extension.style.expressions.dsl.generated.get
import com.mapbox.maps.extension.style.layers.generated.modelLayer
import com.mapbox.maps.extension.style.layers.properties.generated.ModelType
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor
import com.mapbox.maps.extension.style.model.model
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.removeOnMapClickListener
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.model.grib.LevelData
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.RocketSpecType
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherAtPosHour
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.settings.SettingsViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.drawableToBitmap
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


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
            Make3dtrajectory(
                mapViewModel,
                detailsScreenViewModel,
                homeScreenViewModel,
                settingsViewModel
            )
        }
//        else if (settingsViewModel.ippcOnMap.value) {
//            val LAYER_ID = "layer-id"
//            val SOURCE_ID = "source-id"
//            val TOP_LAYER_ID = "line-layer"
//            val SETTLEMENT_LABEL = "settlement-major-label"
//            val SOURCE_URL =
//                "https://raw.githubusercontent.com/relet/pg-xc/master/geojson/luftrom.geojson"
//            MapEffect() { mapView ->
//                mapView.mapboxMap.apply {
//
//                    loadStyle(
//                        style(Style.OUTDOORS) {
//                            +geoJsonSource(SOURCE_ID) {
//                                data(SOURCE_URL)
//                            }
//                            +layerAtPosition(
//                                fillLayer(LAYER_ID, SOURCE_ID) {
//                                    fillColor(Color.parseColor("#0080ff")).fillOpacity(0.7)
//                                },
//                                below = SETTLEMENT_LABEL
//                            )
//                            +lineLayer(
//                                TOP_LAYER_ID, SOURCE_ID
//                            ) {
//
//                                lineWidth(.5)
//                            }
//                        })
//                }
//            }
//        }
        else {
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

@OptIn(MapboxExperimental::class)
@Composable
fun Make3dtrajectory(
    mapViewModel: MapViewModel,
    detailsScreenViewModel: DetailsScreenViewModel,
    homeScreenViewModel: HomeScreenViewModel,
    settingsViewModel: SettingsViewModel,

    ) {
    val SOURCE_ID1 = "source1"
    val redball = "asset://bigball.glb"
    val blueball = "asset://blueball.glb"
    val MODEL_ID_KEY = "model-id-key"
    val MODEL_ID_2 = "model-id-2"
    val SAMPLE_MODEL_URI_2 = "asset://portalrocketv3.glb"
    val cords = Point.fromLngLat(mapViewModel.lon.value, mapViewModel.lat.value)
    val weatherUiState by homeScreenViewModel.weatherUiState.collectAsState()
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
        mapView.mapboxMap.removeOnMapClickListener() { false }

        mapView.mapboxMap.apply {


            loadStyle(
                style(Style.OUTDOORS) {
                    tra.forEachIndexed { index, point ->
                        if (point.z < 0) {
                            return@forEachIndexed
                        } else if ((index % settingsViewModel.rocketSpecMutableStates[RocketSpecType.RESOLUTION.ordinal].doubleValue).toInt() == 0) {
                            val MODEL_ID_1 = "model-id${index}"
                            val SOURCE_ID = "source-id$${index}"
                            val MODEL1_COORDINATES = Point.fromLngLat(
                                mapViewModel.lon.value, mapViewModel.lat.value
                            )
                            if (point.parachuted) {
                                +model(MODEL_ID_1) {
                                    uri(blueball)
                                }
                            } else {
                                +model(MODEL_ID_1) {
                                    uri(redball)
                                }
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

    if (mapViewModel.showTraDetails.value) {
        val lastPoint = tra.last()
        val highestPoint = tra.maxBy { it.z }
        val lastCord =
            offsetLatLon(mapViewModel.lat.value, mapViewModel.lon.value, lastPoint.x, lastPoint.y)
        val highestCord =
            offsetLatLon(
                mapViewModel.lat.value,
                mapViewModel.lon.value,
                highestPoint.x,
                highestPoint.y
            )

        val linePoints = listOf(
            Point.fromLngLat(mapViewModel.lon.value, mapViewModel.lat.value),
            Point.fromLngLat(lastCord.second, lastCord.first)
        )
        val cordStart = Coordinates(mapViewModel.lon.value, mapViewModel.lat.value)
        val cordEnd = Coordinates(lastCord.second, lastCord.first)
        val middleCord = calculateMidpoint(cordStart, cordEnd)
        val distance =
            calcDistance(
                cordStart.latitude,
                cordStart.longitude,
                cordEnd.latitude,
                cordEnd.longitude
            )
        Log.d("Distance", distance.toString())

        PolygonAnnotation(
            points = listOf(
                generateCirclePoints(cordEnd.longitude, cordEnd.latitude, 150.0, 250)
            ), fillColorInt = Color.RED, fillOpacity = 0.5,
            onClick = {
                Log.d("Clicked on", "Red")
                true
            }
        )
        PolygonAnnotation(
            points = listOf(
                generateCirclePoints(highestCord.first, highestCord.second, 150.0, 250)
            ), fillColorInt = Color.GREEN, fillOpacity = 0.5,
            onClick = {
                Log.d("Clicked on", "Green")
                true
            }

        )

        PolylineAnnotation(points = linePoints, lineWidth = 2.0)
        PointAnnotation(
            point = Point.fromLngLat(middleCord.latitude, middleCord.longitude),
            textField = "${String.format("%.2f", distance)} km",
            textAnchor = TextAnchor.TOP_RIGHT,

            )

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


    return 0.0
}

fun offsetLatLon(
    lat: Double,
    lon: Double,
    x_offset: Double,
    y_offset: Double,
): Pair<Double, Double> {
    // Earth's radius in meters
    val R = 6378137.0 // approximate radius of Earth in meters

    // Offset in radians
    val lat_offset = y_offset / R
    val lon_offset = x_offset / (R * cos(Math.PI * lat / 180))

    // New latitude and longitude
    val new_lat = lat + (lat_offset * 180 / Math.PI)
    val new_lon = lon + (lon_offset * 180 / Math.PI)

    return Pair(new_lat, new_lon)
}

data class Coordinates(val latitude: Double, val longitude: Double)

fun calculateMidpoint(coord1: Coordinates, coord2: Coordinates): Coordinates {
    val x1 = Math.toRadians(coord1.latitude)
    val x2 = Math.toRadians(coord2.latitude)
    val x3 = Math.toRadians(coord2.longitude - coord1.longitude)

    val x4 = cos(x2) * cos(x3)
    val x5 = cos(x2) * sin(x3)
    val x6 = atan2(sin(x1) + sin(x2), sqrt((cos(x1) + x4).pow(2.0) + x5.pow(2.0)))
    val x7 = Math.toRadians(coord1.longitude) + atan2(x5, cos(x1) + x4)

    val latitude = Math.toDegrees(x6)
    val longitude = Math.toDegrees(x7)

    return Coordinates(latitude, longitude)
}

fun calcDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371 // Earth radius in kilometers
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return R * c
}

fun generateCirclePoints(lat: Double, lon: Double, radius: Double, numPoints: Int): List<Point> {
    val circlePoints = mutableListOf<Point>()
    for (i in 0..numPoints) {
        val angle = Math.toRadians(i * (360.0 / numPoints))
        val dx = radius * cos(angle)
        val dy = radius * sin(angle)
        val circleLat = lat + (180 / PI) * (dy / 6378137.0)
        val circleLon = lon + (180 / PI) * (dx / 6378137.0) / cos(Math.toRadians(lat))
        circlePoints.add(Point.fromLngLat(circleLon, circleLat))
    }
    return circlePoints
}