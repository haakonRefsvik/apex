package no.uio.ifi.in2000.rakettoppskytning

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.mapbox.geojson.Point
import com.mapbox.maps.MapDebugOptions
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.MapboxMapScope
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.annotation.AnnotationManagerImpl
import com.mapbox.maps.plugin.attribution.generated.AttributionSettings
import com.mapbox.maps.plugin.compass.generated.CompassSettings
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings
import com.mapbox.maps.plugin.scalebar.generated.ScaleBarSettings
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class, MapboxExperimental::class)
@Composable
fun HomeScreen(
    homeScreenViewModel: HomeScreenViewModel = viewModel()
    ) {

    val forecast by homeScreenViewModel.foreCastUiState.collectAsState()
    var lat by remember { mutableDoubleStateOf(59.9434927) }
    var lon by remember { mutableDoubleStateOf(10.71181022) }


    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }


    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },

        topBar = {
            TopAppBar(
                title = {

                    ClickableText(
                        text = AnnotatedString(
                            text = "Gå til hjemskjerm",
                            spanStyle = SpanStyle(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 20.sp
                            )
                        ),
                        onClick = { },

                        )

                },
                navigationIcon = {

                },
            )
        },
        bottomBar = {
            BottomAppBar {
                if(forecast.foreCast.isEmpty()){
                    Text(text = "Trykk på kart for å se grader på det stedet")
                    
                }
                forecast.foreCast.forEach{
                        input ->
                    Column {
                        val tider = input.properties.timeseries[0]
                        Text(" ${tider.data.instant.details.airTemperature}°C" )
                        tider.data.next1Hours?.summary?.let { Text(it.symbolCode) }


                    }





                }


            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            val mapViewportState = rememberMapViewportState {
            setCameraOptions {
                center(Point.fromLngLat(lon, lat))
                zoom(5.0)
                pitch(0.0)
            }
        }

            val mapBoxUiSettings: GesturesSettings by remember {
                mutableStateOf(GesturesSettings {
                    rotateEnabled = false
                    pinchToZoomEnabled = true
                    pitchEnabled = false
                })
            }

            val compassSettings: CompassSettings by remember {
                mutableStateOf(CompassSettings { enabled = true })
            }

            val scaleBarSetting: ScaleBarSettings by remember {
                mutableStateOf(ScaleBarSettings { enabled = false })
            }

            MapboxMap(
                modifier = Modifier.fillMaxSize(),
                mapInitOptionsFactory = { context ->
                    MapInitOptions(
                        context = context,

                    )
                },
                mapViewportState = mapViewportState,
                compassSettings = compassSettings,
                scaleBarSettings = scaleBarSetting,
                gesturesSettings = mapBoxUiSettings,
                attributionSettings = AttributionSettings {
                    enabled = false
                },
            ) {

                LaunchedEffect(Unit) {
                    delay(200)
                    mapViewportState.flyTo(
                        cameraOptions = cameraOptions {
                            center(Point.fromLngLat(lon, lat))
                            zoom(10.0)
                        },
                        animationOptions = MapAnimationOptions.mapAnimationOptions { duration(2000) },
                    )
                }
                MapEffect(Unit) { mapView ->
                    // Use mapView to access all the Mapbox Maps APIs including plugins etc.
                    // For example, to enable debug mode:
                    mapView.mapboxMap.addOnMapClickListener {
                        Log.d("s", "${it.latitude()},${it.longitude()}")
                        homeScreenViewModel.getForecastByCord(it.latitude(),it.longitude())
                        false
                    }


                }
            }

        }

    }



}