package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import kotlinx.coroutines.launch


/** The inputfield where you can search for the weather at a spesific lat/lon */
@OptIn(MapboxExperimental::class, ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InputField(homeScreenViewModel: HomeScreenViewModel){
    val lat by homeScreenViewModel.lat
    val lon by homeScreenViewModel.lon
    val controller = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(lon, lat))
            zoom(10.5)
            pitch(0.0)
        }
    }
    val scope = rememberCoroutineScope()
    val scaffoldState by homeScreenViewModel.bottomSheetScaffoldState

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
            homeScreenViewModel.getVerticalProfileByCord(lat, lon)
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
            homeScreenViewModel.getVerticalProfileByCord(lat, lon)
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
}