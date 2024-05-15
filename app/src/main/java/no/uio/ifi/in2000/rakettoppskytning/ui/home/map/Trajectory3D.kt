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
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
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

    if (tra.isEmpty()) return

    val topPoint = tra.firstOrNull { it.isParachuted() }?: tra[0]
    val topPointIndex = tra.indexOf(topPoint)
    val rocketPointIndex = (topPointIndex / 3)
    val rocketPoint = tra[rocketPointIndex]
    var paraCord = tra.find { it.isParachuted() && (it.getZ() < topPoint.getZ() / 2) }
    if (paraCord == null) {
        paraCord = tra.find { it.isParachuted() }
    }

    val pitch = try{
        calculatePitchAndYaw(rocketPoint, tra[rocketPointIndex + 10])
    }catch (_: Exception){
        Pair(0.0, 0.0)
    }

    if (mapViewModel.showTraDetails.value) {
        ShowTraDetails(mapViewModel = mapViewModel, tra = tra)
    }
    MapEffect { mapView ->
        mapView.mapboxMap.removeOnMapClickListener { false }
        mapView.mapboxMap.apply {
            loadStyle(
                style(Style.OUTDOORS) {
                    /**Goes through the list of xyz points and based on the parachuted value, plots a ball on the given xyz on tha map**/
                    tra.forEachIndexed { index, point ->
                        if (point.getZ() < 0) {
                            return@forEachIndexed
                        } else if ((index % settingsViewModel.rocketSpecMutableStates[RocketSpecType.RESOLUTION.ordinal].doubleValue).toInt() == 0) {
                            val modelId1 = "model-id${index}"
                            val sourceId = "source-id$${index}"
                            val model1Pos = Point.fromLngLat(
                                mapViewModel.lon.value, mapViewModel.lat.value
                            )
                            if (point.isParachuted()) {
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
                                        point.getX(),
                                        point.getY() * -1,
                                        point.getZ()
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
                        modelTranslation(
                            listOf(
                                rocketPoint.getX(),
                                rocketPoint.getY() * -1,
                                rocketPoint.getZ()
                            )
                        )

                        val eulerAngle = convertPitchYawToEuler(
                            pitch = pitch.first,
                            yaw = pitch.second
                        )

                        modelRotation(
                            listOf(
                                eulerAngle.lon,
                                eulerAngle.lat * -1,
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
                            modelTranslation(listOf(paraCord.getX(), paraCord.getY() * -1, paraCord.getZ()))
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

/**
 * This is a function from Chat-GPT to convert
 * pitch and yaw radiant to Euler-angles for the rocket-model
 * */
data class EulerAngle(val lon: Double, val lat: Double)
fun convertPitchYawToEuler(pitch: Double, yaw: Double): EulerAngle {
    val lon = atan2(cos(pitch) * sin(yaw), sin(pitch))
    val lat = asin(-cos(pitch) * cos(yaw))
    return EulerAngle(Math.toDegrees(lon), Math.toDegrees(lat))
}

/**Shows a line from the start point to the landing coordinates and shows the distance**/
@OptIn(MapboxExperimental::class)
@Composable
fun ShowTraDetails(
    mapViewModel: MapViewModel,
    tra: List<no.uio.ifi.in2000.rakettoppskytning.model.trajectory.Point>
) {
    val lastPoint = tra.last()
    val lastParaPoint = tra.find { it.getZ() <= 10 && it.isParachuted() }
    val lastCord =
        offsetLatLon(mapViewModel.lat.value, mapViewModel.lon.value, lastPoint.getX(), lastPoint.getY())


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
            lastParaPoint.getX(),
            lastParaPoint.getY()
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

/**
 * This is a function from Chat-GPT to get the correct pitch and
 * yaw for the rocket-model.
 * */
fun calculatePitchAndYaw(
    start: no.uio.ifi.in2000.rakettoppskytning.model.trajectory.Point,
    end: no.uio.ifi.in2000.rakettoppskytning.model.trajectory.Point
): Pair<Double, Double> {
    val dx = end.getX() - start.getX()
    val dy = end.getY() - start.getY()
    val dz = end.getZ() - start.getZ()

    val distanceXY = hypot(dx, dy)
    val pitch = atan2(dz, distanceXY)

    val yaw = atan2(dy, dx)

    return Pair(pitch, yaw)
}

/**
 * This is a function from Chat-GPT to get a lat and lon from
 * a starting-position and offset in meters
 * */
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

/**This function calculates the distance between two lat lon pairs in kilometres
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

/**This algorithm has the only purpose of generating a circle of lat-lon points around a given lat lon.
 * Mapbox has circle-annotations, but when you pitch the camera it doesn't stay flat on the map.
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