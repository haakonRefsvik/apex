package no.uio.ifi.in2000.rakettoppskytning

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.substring
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.gson.Gson
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
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, MapboxExperimental::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    homeScreenViewModel: HomeScreenViewModel = viewModel()
    ) {

    val forecast by homeScreenViewModel.foreCastUiState.collectAsState()
    var lat by remember { mutableDoubleStateOf(59.9434927) }
    var lon by remember { mutableDoubleStateOf(10.71181022) }


    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(lon, lat))
            zoom(11.0)
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


    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },

        topBar = {
            TopAppBar(


                title = {

                    ClickableText(


                        text = AnnotatedString(
                            text = "ToppAppBar",
                            spanStyle = SpanStyle(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 15.sp
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
            BottomAppBar() {
                Text(text = "BottomAppBar")



            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            val scaffoldState = rememberBottomSheetScaffoldState()
            BottomSheetScaffold(scaffoldState  = scaffoldState, sheetPeekHeight = 160.dp, sheetContent ={
                Box{
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,


                        content =
                        {


                            val currentInstant = Instant.now().plus(1,ChronoUnit.HOURS)
                            val formatter = DateTimeFormatter.ISO_INSTANT


                            val formattedInstant = formatter.format(currentInstant)

                            val newInstant = currentInstant.plus(6, ChronoUnit.HOURS)

                            val formattedInstantAfter = formatter.format(newInstant)
                            Row {
                                TextField(value = lat.toString(), onValueChange ={}, Modifier.width(160.dp), textStyle = TextStyle(fontSize = 12.sp))
                                Spacer(modifier = Modifier.width(20.dp))
                                TextField(value = lon.toString(), onValueChange ={}, Modifier.width(160.dp) , textStyle = TextStyle(fontSize = 12.sp))

                            }
                            Spacer(modifier = Modifier.height(5.dp))
                            Button(onClick = { scope.launch{scaffoldState.bottomSheetState.expand()
                                homeScreenViewModel.getForecastByCord(lat,lon)
                                    mapViewportState.flyTo(
                                        cameraOptions = cameraOptions {
                                        center(Point.fromLngLat(lon, lat))

                                    },)} }) {
                                Text("Hent værdata")

                            }
                            Spacer(modifier = Modifier.height(5.dp))
                            LazyColumn(content = { item {
                                forecast.foreCast.forEach breaking@{
                                        input ->
                                    input.properties.timeseries.forEach lit@{
                                            tider->
                                        if( tider.time < formattedInstant){
                                            return@lit
                                        }
                                        if(formattedInstant < tider.time && tider.time < formattedInstantAfter){
                                            val klokkeslett = tider.time.substring(11,16)
                                            Spacer(modifier = Modifier.height(7.5.dp))
                                            ElevatedCard(
                                                modifier = Modifier
                                                    .height(100.dp)
                                                    .width(340.dp),
                                                onClick ={
                                                    val json = Uri.encode(Gson().toJson(tider.data.instant.details))
                                                    Log.d("asdf",json)
                                                    navController.navigate("DetailsScreen/${json}")})
                                            {



                                                Text("Kl:$klokkeslett")
                                                Text(" ${tider.data.instant.details.airTemperature}°C" )
                                                tider.data.next1Hours?.summary?.let { Text(it.symbolCode) }



                                            }

                                            Spacer(modifier = Modifier.height(7.5.dp))




                                        }
                                        else{
                                            return@breaking
                                        }

                                    }

                                }

                            } })





                })

                }



            } ) {

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
                        mapView.mapboxMap.addOnMapClickListener {
                            Log.d("s", "${it.latitude()},${it.longitude()}")

                            lat = it.latitude()
                            lon = it.longitude()
                            false
                        }


                    }
                }


            }





        }

    }



}