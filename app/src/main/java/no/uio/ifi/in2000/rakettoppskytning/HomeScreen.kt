package no.uio.ifi.in2000.rakettoppskytning

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    homeScreenViewModel: HomeScreenViewModel = viewModel()
    ) {
    val customFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val forecast by homeScreenViewModel.foreCastUiState.collectAsState()
    var lat by remember { mutableDoubleStateOf(59.9434927) }
    var lon by remember { mutableDoubleStateOf(10.71181022) }
    val ifi = LatLng(lat, lon)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(ifi, 12f)
    }

    val currentDateTime = LocalDateTime.now()
    val newDateTime = currentDateTime.plusHours(1)
    val formattedDateTime = newDateTime.format(customFormatter)
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
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Gå til hjemskjerm"
                        )
                    }
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
                        input.properties.timeseries.forEach {
                                tider ->
                            if (formattedDateTime.toString() > tider.time && currentDateTime.toString() < tider.time){
                                Log.d("asd",tider.time)
                                Text(" ${tider.data.instant.details.airTemperature}°C" )
                                tider.data.next1Hours?.summary?.let { Text(it.symbolCode) }
                                return@forEach
                            }


                        }

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
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = {homeScreenViewModel.getForecastByCord(it.latitude, it.longitude)
                lat = it.latitude
                lon = it.longitude}
            ) {


                Marker(
                    state = MarkerState(position = ifi),
                    title = "Ole-Johan Dahls hus ",
                    snippet = "Marker på IFI"
                )
            }
        }

    }



}