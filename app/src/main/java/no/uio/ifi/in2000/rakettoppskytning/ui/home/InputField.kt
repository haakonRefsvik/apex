package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapbox.maps.MapboxExperimental
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteEvent
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteState
import no.uio.ifi.in2000.rakettoppskytning.network.InternetConnectionViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.favorite.AddFavoriteDialog

fun formatNewValue(input: String): Double {
    val onlyDigitsAndDot = input.filter { it.isDigit() || it == '.' || it == '-' }

    val decimalParts = onlyDigitsAndDot.split(".")
    val integerPart = decimalParts.getOrNull(0) ?: ""

    var formattedIntegerValue = integerPart

    while (formattedIntegerValue.length > 2) {
        formattedIntegerValue = formattedIntegerValue.dropLast(1)
    }

    val decimalPart = if (decimalParts.size > 1) {
        "." + decimalParts[1]  // Reconstruct the decimal part, if present
    } else {
        ""
    }

    val r = (formattedIntegerValue + decimalPart)

    return (r).toDouble()
}

/** The inputfield where you can search for the weather at a spesific lat/lon */

@OptIn(MapboxExperimental::class, ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InputField(
    homeScreenViewModel: HomeScreenViewModel,
    mapViewModel: MapViewModel,
    state: FavoriteState,
    onEvent: (FavoriteEvent) -> Unit,
    internetConnectionViewModel: InternetConnectionViewModel,
) {

    val isInternetConnected by internetConnectionViewModel.isInternetConnected.observeAsState()

    Log.d("intenet: ", isInternetConnected.toString())


    val showDecimals = 5

    val lat by mapViewModel.lat
    val lon by mapViewModel.lon


    val controller = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val scope = rememberCoroutineScope()
    val scaffoldState by homeScreenViewModel.bottomSheetScaffoldState


    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Row {
            OutlinedTextField(
                value = String.format(
                    "%.${showDecimals}f",
                    lat
                ), // viser lat, verdien som maks 5 desimaler
                onValueChange = { input ->
                    mapViewModel.lat.value = formatNewValue(input)
                },
                Modifier
                    .width(130.dp)
                    .height(58.dp),
                textStyle = TextStyle(fontSize = 18.sp),
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
                singleLine = true,
            )
            Spacer(modifier = Modifier.width(50.dp))
            OutlinedTextField(
                value = String.format(
                    "%.${showDecimals}f",
                    lon
                ), // viser lat, verdien som maks 5 desimaler
                onValueChange = { input ->
                    mapViewModel.lon.value = formatNewValue(input)
                },
                Modifier
                    .width(130.dp)
                    .height(58.dp),

                textStyle = TextStyle(fontSize = 18.sp),
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
                singleLine = true
            )

        }
        Spacer(modifier = Modifier.height(20.dp))
        Row {
            Log.d("moveCam -1: ", "lat: ${lat} og lon: ${lon}")
            mapViewModel.moveMapCamera(lat, lon)

            OutlinedButton(modifier = Modifier.width(155.dp), onClick = {
                controller?.hide()
                mapViewModel.lat.value = lat
                mapViewModel.lon.value = lon

                //TODO: HER SKAL POSISJONEN TIL KARTET OPPDATERES
                mapViewModel.updateCamera(lat, lon)
                scope.launch {
                    delay(1000)
                    onEvent(FavoriteEvent.ShowDialog)
                }

            },
                enabled = isInternetConnected?: false
            ) {
                Log.d("Før addingFav: ", "lat: ${lat} og lon: ${lon}")

                if (state.isAddingFavorite) {
                    Log.d("addingFav: ", "lat: ${lat} og lon: ${lon}")
                    AddFavoriteDialog(
                        state = state,
                        onEvent = onEvent,
                        lat = lat,
                        lon = lon,
                        mapViewModel
                    )
                }
                Text("Legg til favoritter")
            }
            Spacer(modifier = Modifier.width(25.dp))
            Button(modifier = Modifier.width(155.dp), onClick = {
                controller?.hide()
                homeScreenViewModel.getWeatherByCord(lat, lon, 24)
                mapViewModel.moveMapCamera(lat, lon)

                scope.launch {
                    delay(1000)
                    scaffoldState.bottomSheetState.expand()
                }
            }) {
                Text("Hent værdata")
            }
            Spacer(modifier = Modifier.height(70.dp))

        }
        Spacer(modifier = Modifier.height(5.dp))

        LazyRow(
            modifier = Modifier.width(340.dp)
        )
        {

            state.favorites.reversed().forEach { favorite ->
                item {
                    ElevatedCard(
                        modifier = Modifier
                            .height(80.dp)
                            .width(120.dp),
                        onClick = {
                            mapViewModel.lat.value = favorite.lat.toDouble()
                            mapViewModel.lon.value = favorite.lon.toDouble()

                            controller?.hide()
                            homeScreenViewModel.getWeatherByCord(lat, lon, 24)
                            mapViewModel.moveMapCamera(lat, lon)

                            scope.launch {
                                delay(1000)
                                scaffoldState.bottomSheetState.expand()
                            }

                        }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.End


                        ) {
                            IconButton(modifier = Modifier
                                .size(35.dp)
                                .padding(8.dp),
                                onClick = {
                                onEvent(FavoriteEvent.DeleteFavorite(favorite))

                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Delete favorite"
                                )
                            }


                        }
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(favorite.name)

                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))
                }
            }


        }
    }
}