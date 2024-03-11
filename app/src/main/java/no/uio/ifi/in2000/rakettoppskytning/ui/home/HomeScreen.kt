package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.sharp.LocationOn
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.gson.Gson
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
import com.mapbox.maps.plugin.compass.generated.CompassSettings
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings
import com.mapbox.maps.plugin.scalebar.generated.ScaleBarSettings
import com.mapbox.maps.viewannotation.annotationAnchors
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.ForeCastSymbols
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun String.isDouble(): Boolean {
    return try {
        this.toDouble()
        true
    } catch (e: NumberFormatException) {
        false
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, MapboxExperimental::class, ExperimentalComposeUiApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    homeScreenViewModel: HomeScreenViewModel = viewModel(),
    context: Context
) {

    val forecast by homeScreenViewModel.foreCastUiState.collectAsState()
    val lat by homeScreenViewModel.lat
    val lon by homeScreenViewModel.lon
    val controller = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    //val scaffoldState = rememberBottomSheetScaffoldState()
    val scaffoldState by homeScreenViewModel.bottomSheetScaffoldState
    val favoritter = listOf<String>(
        "Lokasjon1",
        "Lokasjon2",
        "Lokasjon3",
        "Lokasjon4",
        "Lokasjon5",
        "Lokasjon6",
        "Lokasjon7",
        "Lokasjon8",
        "Lokasjon9",
        "Lokasjon10",
    )


    /*** HUSKE Å LEGGE TIL UISATE SLIK AT TING BLIR HUSKET NÅR MAN NAVIGERER!!***/


    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { scaffoldState.snackbarHostState }
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(lon, lat))
            zoom(10.5)
            pitch(0.0)
        }
    }

    val mapBoxUiSettings: GesturesSettings by remember {
        mutableStateOf(GesturesSettings {
            rotateEnabled = false
            pinchToZoomEnabled = true
            pitchEnabled = true
        })
    }

    val compassSettings: CompassSettings by remember {
        mutableStateOf(CompassSettings { enabled = true })
    }

    val scaleBarSetting: ScaleBarSettings by remember {
        mutableStateOf(ScaleBarSettings { enabled = false })
    }


    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },

        topBar = {
            TopAppBar(
                title = {}, modifier = Modifier
                    .background(Color.Transparent)
                    .height(0.dp)

            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(

                            Icons.Sharp.LocationOn,
                            modifier = Modifier.size(40.dp),
                            contentDescription = "Location"
                        )
                    }
                    Spacer(modifier = Modifier.width(94.dp))
                    IconButton(onClick = { /*TODO*/ }) {
                        Image(
                            painter = painterResource(R.drawable.rakket),
                            contentDescription = "Rakket"
                        )
                    }
                    Spacer(modifier = Modifier.width(95.dp))
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.Sharp.Settings,
                            modifier = Modifier.size(40.dp),
                            contentDescription = "Settings"
                        )
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

            BottomSheetScaffold(
                scaffoldState = scaffoldState,
                sheetPeekHeight = 156.dp,
                sheetContent = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,

                        content =
                        {


                            val currentInstant = Instant.now()
                            val formatter = DateTimeFormatter.ISO_INSTANT


                            val formattedInstant = formatter.format(currentInstant)

                            val newInstant = currentInstant.plus(7, ChronoUnit.HOURS)

                            val formattedInstantAfter = formatter.format(newInstant)
                            Row {
                                OutlinedTextField(
                                    value = lat.toString(),
                                    onValueChange = { value ->
                                        if (value.isDouble()) {
                                            homeScreenViewModel.lat.value =
                                                value.toDouble().coerceIn(-90.0, 90.0)
                                        }
                                    },
                                    Modifier
                                        .width(170.dp)
                                        .height(52.dp),
                                    textStyle = TextStyle(fontSize = 12.sp),
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Done,
                                        keyboardType = KeyboardType.Number
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            controller?.hide()
                                            focusManager.clearFocus()
                                        }
                                    ),
                                    label = { Text("Longitude") },
                                    singleLine = true,
                                )
                                Spacer(modifier = Modifier.width(20.dp))
                                OutlinedTextField(value = lon.toString(),
                                    onValueChange = { value ->
                                        if (value.isDouble()) {
                                            homeScreenViewModel.lon.value = if (value.toDouble()
                                                    .isInfinite()
                                            ) 0.0 else value.toDouble()
                                        }
                                    },

                                    Modifier
                                        .width(160.dp)
                                        .height(52.dp),
                                    textStyle = TextStyle(fontSize = 12.sp),
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Done,
                                        keyboardType = KeyboardType.Number
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            controller?.hide()
                                            focusManager.clearFocus()
                                        }
                                    ),
                                    label = { Text("Latitude") },
                                    singleLine = true
                                )

                            }
                            Spacer(modifier = Modifier.height(5.dp))
                            Row {
                                OutlinedButton(modifier = Modifier.width(155.dp), onClick = {
                                    controller?.hide()
                                    homeScreenViewModel.getForecastByCord(lat, lon)
                                    mapViewportState.flyTo(
                                        cameraOptions = cameraOptions {
                                            center(Point.fromLngLat(lon, lat))

                                        },
                                    )
                                    scope.launch {
                                        scaffoldState.bottomSheetState.expand()
                                    }
                                }) {
                                    Text("Legg til favoritter")

                                }
                                Spacer(modifier = Modifier.width(25.dp))
                                Button(modifier = Modifier.width(155.dp), onClick = {
                                    controller?.hide()
                                    homeScreenViewModel.getForecastByCord(lat, lon)
                                    mapViewportState.flyTo(
                                        cameraOptions = cameraOptions {
                                            center(Point.fromLngLat(lon, lat))

                                        },
                                    )
                                    scope.launch {
                                        scaffoldState.bottomSheetState.expand()
                                    }
                                }) {
                                    Text("Hent værdata")

                                }

                            }

                            Spacer(modifier = Modifier.height(5.dp))
                            LazyRow(
                                modifier = Modifier.width(340.dp),

                                content = {
                                    favoritter.forEach {
                                        item {
                                            ElevatedCard(
                                                modifier = Modifier
                                                    .height(80.dp)
                                                    .width(120.dp)
                                            ) {
                                                Text(it)

                                            }
                                            Spacer(modifier = Modifier.width(20.dp))
                                        }
                                    }
                                })
                            Spacer(modifier = Modifier.height(10.dp))
                            WeatherColumn(
                                homeScreenViewModel = homeScreenViewModel,
                                navController = navController
                            )
                        })


                }) {


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


            }

        }


    }
}

