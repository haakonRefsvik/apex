package no.uio.ifi.in2000.rakettoppskytning.ui.home.map

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
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
import com.mapbox.maps.plugin.gestures.removeOnMapClickListener
import no.uio.ifi.in2000.rakettoppskytning.model.grib.LevelData
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.RocketSpecType
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherAtPosHour
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.settings.SettingsViewModel
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**Adds the simulated trajectory to the map**/
@OptIn(MapboxExperimental::class)
@Composable
fun Make3Dtrajectory(
    mapViewModel: MapViewModel,
    detailsScreenViewModel: DetailsScreenViewModel,
    homeScreenViewModel: HomeScreenViewModel,
    settingsViewModel: SettingsViewModel,

    ) {
    val sourceId1 = "source1"
    val sourceId2 = "source2"
    val paraId = "paraId"
    val redBall = "asset://bigball.glb"
    val blueBall = "asset://blueball.glb"
    val parachute = "asset://parachute.glb"
    val modelIdKey = "model-id-key"
    val modelId2 = "model-id-2"
    val sampleModelUri2 = "asset://portalrocketSimpleMaterials.glb"
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
        weatherAtPosHour.firstOrNull()?.verticalProfile?.getAllLevelData() ?: listOf()

    mapViewModel.loadTrajectory(allLevels, rocketSpecs)
    val tra = mapViewModel.trajectory.value

    val rocketPoint = tra.find { it.z in 600.0..1000.0 }
    var paraCord = tra.find { it.parachuted && (it.z in 2200.0..2500.0) }
    if (paraCord == null) {
        paraCord = tra.find { it.parachuted }
    }


    val rocketPointIndex = tra.indexOf(rocketPoint)
    var pitch = Pair(0.0, 0.0)
    if (rocketPoint != null) {
        pitch = calculatePitch(rocketPoint, tra[rocketPointIndex + 20])
    }

    if (mapViewModel.showTraDetails.value) {
        ShowTraDetails(mapViewModel = mapViewModel, tra = tra)
    }
    MapEffect { mapView ->
        mapView.mapboxMap.removeOnMapClickListener { false }
        mapView.mapboxMap.apply {
            loadStyle(
                style(Style.OUTDOORS) {
                    /**Goes thorugh the list of xyz points and based on the parachuted value, plots a ball on the given xyz on tha map**/
                    tra.forEachIndexed { index, point ->
                        if (point.z < 0) {
                            return@forEachIndexed
                        } else if ((index % settingsViewModel.rocketSpecMutableStates[RocketSpecType.RESOLUTION.ordinal].doubleValue).toInt() == 0) {
                            val modelId1 = "model-id${index}"
                            val sourceId = "source-id$${index}"
                            val model1Pos = Point.fromLngLat(
                                mapViewModel.lon.value, mapViewModel.lat.value
                            )
                            if (point.parachuted) {
                                +model(modelId1) {
                                    uri(blueBall)
                                }
                            } else {
                                +model(modelId1) {
                                    uri(redBall)
                                }
                            }

                            +geoJsonSource(sourceId) {
                                featureCollection(
                                    FeatureCollection.fromFeatures(

                                        listOf(
                                            Feature.fromGeometry(model1Pos)
                                                .also {
                                                    it.addStringProperty(
                                                        modelIdKey,
                                                        modelId1
                                                    )
                                                },

                                            )
                                    )
                                )
                            }

                            +modelLayer(modelId1, sourceId) {
                                modelId(get(modelIdKey))
                                modelType(ModelType.COMMON_3D)
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

                    +model(modelId2) {
                        uri(sampleModelUri2)
                    }


                    +geoJsonSource(sourceId1) {
                        featureCollection(
                            FeatureCollection.fromFeatures(

                                listOf(

                                    Feature.fromGeometry(cords)
                                        .also {
                                            it.addStringProperty(
                                                modelIdKey,
                                                modelId2,
                                            )
                                        },

                                    )
                            )
                        )
                    }

                    +modelLayer(modelId2, sourceId1) {
                        modelId(get(modelIdKey))
                        modelType(ModelType.COMMON_3D)
                        modelScale(listOf(300.0, 300.0, 300.0))
                        if (rocketPoint != null) {
                            modelTranslation(
                                listOf(
                                    rocketPoint.x,
                                    rocketPoint.y * -1,
                                    rocketPoint.z
                                )
                            )
                        }

                        modelRotation(
                            listOf(
                                pitch.first * (rocketSpecs.launchAngle.toDouble() * getPitchRocketModel(
                                    rocketSpecs.launchAngle.toDouble()
                                )) * -1,
                                pitch.second * (rocketSpecs.launchAngle.toDouble() * getPitchRocketModel(
                                    rocketSpecs.launchAngle.toDouble()
                                )),
                                0.0
                            )
                        )

                        modelCastShadows(true)
                        modelReceiveShadows(true)
                        modelRoughness(0.1)
                    }

                    if (paraCord != null) {
                        +model(paraId) {
                            uri(parachute)
                        }


                        +geoJsonSource(sourceId2) {
                            featureCollection(
                                FeatureCollection.fromFeatures(

                                    listOf(

                                        Feature.fromGeometry(cords)
                                            .also {
                                                it.addStringProperty(
                                                    modelIdKey,
                                                    paraId,
                                                )
                                            },

                                        )
                                )
                            )
                        }

                        +modelLayer(paraId, sourceId2) {
                            modelId(get(modelIdKey))
                            modelType(ModelType.COMMON_3D)
                            modelScale(listOf(50.0, 50.0, 50.0))
                            modelTranslation(listOf(paraCord.x, paraCord.y * -1, paraCord.z))
                            modelCastShadows(true)
                            modelReceiveShadows(true)
                            modelRoughness(0.1)
                        }
                    }
                }
            )
        }
    }
}

/**Shows a line from the start point to the landing coordinates and shows the distance**/
@OptIn(MapboxExperimental::class)
@Composable
fun ShowTraDetails(
    mapViewModel: MapViewModel,
    tra: List<no.uio.ifi.in2000.rakettoppskytning.data.ballistic.Point>
) {
    val lastPoint = tra.last()
    val lastParaPoint = tra.find { it.z <= 10 && it.parachuted }
    val lastCord =
        offsetLatLon(mapViewModel.lat.value, mapViewModel.lon.value, lastPoint.x, lastPoint.y)


    val linePoints = listOf(
        Point.fromLngLat(mapViewModel.lon.value, mapViewModel.lat.value),
        Point.fromLngLat(lastCord.second, lastCord.first),

        )
    val cordStart = Coordinates(mapViewModel.lat.value, mapViewModel.lon.value)
    val cordEnd = Coordinates(lastCord.first, lastCord.second)
    val middleCord = calculateMidpoint(cordStart, cordEnd)
    val distance =
        calcDistance(
            cordStart.latitude,
            cordStart.longitude,
            cordEnd.latitude,
            cordEnd.longitude
        )
    if (lastParaPoint != null) {

        val lastParaCord = offsetLatLon(
            mapViewModel.lat.value,
            mapViewModel.lon.value,
            lastParaPoint.x,
            lastParaPoint.y
        )
        val middleCordPara =
            calculateMidpoint(cordStart, Coordinates(lastParaCord.first, lastParaCord.second))
        val lastParaPoints = listOf(
            Point.fromLngLat(mapViewModel.lon.value, mapViewModel.lat.value),
            Point.fromLngLat(lastParaCord.second, lastParaCord.first),
        )
        PolylineAnnotation(points = lastParaPoints, lineWidth = 2.0, lineColorInt = Color.BLUE)
        PolygonAnnotation(
            points = listOf(
                generateCirclePoints(lastParaCord.first, lastParaCord.second, 150.0, 250)
            ), fillColorInt = Color.BLUE, fillOpacity = 0.5,
            onClick = {

                true
            }

        )
        val paraDistance = calcDistance(
            mapViewModel.lat.value,
            mapViewModel.lon.value,
            lastParaCord.first,
            lastParaCord.second
        )
        PointAnnotation(
            point = Point.fromLngLat(middleCordPara.longitude, middleCordPara.latitude),
            textField = "${String.format("%.2f", paraDistance)} km",
            textAnchor = TextAnchor.TOP_RIGHT,
            textColorInt = Color.BLUE,
        )


    }
    PolygonAnnotation(
        points = listOf(
            generateCirclePoints(cordEnd.latitude, cordEnd.longitude, 150.0, 250)
        ), fillColorInt = Color.RED, fillOpacity = 0.5,
        onClick = {
            true
        }
    )


    PolylineAnnotation(points = linePoints, lineWidth = 2.0, lineColorInt = Color.RED)
    PointAnnotation(
        point = Point.fromLngLat(middleCord.longitude, middleCord.latitude),
        textField = "${String.format("%.2f", distance)} km",
        textAnchor = TextAnchor.TOP_RIGHT,
        textColorInt = Color.RED

    )


}

/**Function that tries to calcualte how much the rocket needs to be rotated on the x and y axis
 *  based on a start point and end point
 *  This algorithm is made with the help of ChatGPT**/
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

/**This function is to offest the calculatePitch, and the eye has been used to get these numbers,
 * so its not accurate, the input is the launch angle of the rocket**/
fun getPitchRocketModel(number: Double): Double {
    return when (number) {
        in 85.0..95.0 -> -0.68
        in 80.0..85.0 -> -0.73
        in 75.0..80.0 -> -0.81
        in 70.0..75.0 -> -0.8
        in 65.0..70.0 -> -0.888
        in 60.0..65.0 -> -0.96
        in 55.0..60.0 -> -1.36
        in 50.0..55.0 -> -1.4
        in 45.0..50.0 -> -1.8
        in 40.0..45.0 -> -2.15
        in 35.0..40.0 -> -2.73
        else -> 0.0
    }
}

/**The algortihm takes in lat and lon, with how many meters an offest has been set in meters. it then
 * returns the the lat and lon based on how many meters the x and y offset is
 * This algorithm is made with the help of ChatGPT**/
fun offsetLatLon(
    lat: Double,
    lon: Double,
    xOffset: Double,
    yOffset: Double,
): Pair<Double, Double> {
    // Earth's radius in meters
    val r = 6378137.0 // approximate radius of Earth in meters

    // Offset in radians
    val latOffset = yOffset / r
    val lonOffset = xOffset / (r * cos(Math.PI * lat / 180))

    // New latitude and longitude
    val newLat = lat + (latOffset * 180 / Math.PI)
    val newLon = lon + (lonOffset * 180 / Math.PI)

    return Pair(newLat, newLon)
}

data class Coordinates(val latitude: Double, val longitude: Double)

/**This algorithm takes in two coordiantes, and calculates what the coordinate in the middle of the input
 * coordinates are, this is only being used to know where to put a pointannotion with text
 * This algorithm is made with the help of ChatGPT**/
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

/**This function calculates the distance between two lat lon pairs in kilometrs
 * This algorithm is made with the help of ChatGPT**/
fun calcDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371 // Earth radius in kilometers
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
}

/**This algorthim has the only purpose of generating a circle of lat-lon points around a given lat lon.
 * Mapbox has circleannotions, but when you pitch the camera it doesnt stay flat on the map.
 * This algorithm is made with the help of ChatGPT**/
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