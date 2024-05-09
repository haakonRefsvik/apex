package no.uio.ifi.in2000.rakettoppskytning.ui.home


import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.maps.MapboxExperimental
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.data.airspace.getAirspace
import no.uio.ifi.in2000.rakettoppskytning.model.formatting.formatNewValue
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteEvent
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteState
import no.uio.ifi.in2000.rakettoppskytning.ui.home.favorite.AddFavoriteDialog
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.firstButton0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.firstButton100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.firstButton50
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main100


/** The inputfield where you can search for the weather at a spesific lat/lon */

@OptIn(MapboxExperimental::class, ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InputField(
    homeScreenViewModel: HomeScreenViewModel,
    mapViewModel: MapViewModel,
    state: FavoriteState,
    onEvent: (FavoriteEvent) -> Unit,
    context: Context
) {

    val showDecimals = 5

    val lat by mapViewModel.lat
    val lon by mapViewModel.lon

    val controller = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val scaffoldState by homeScreenViewModel.bottomSheetScaffoldState
    val isAddingFavorite by remember(state.isAddingFavorite) { mutableStateOf(state.isAddingFavorite) }


    if (isAddingFavorite) {
        AddFavoriteDialog(
            state = state,
            onEvent = onEvent,
            lat = lat,
            lon = lon,
            context
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = main100),
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
                    try {
                        mapViewModel.lat.value = formatNewValue(
                            input,
                            2,
                            5,
                            highestInput = 90.0,
                            lowestInput = -90.0,
                            oldValue = mapViewModel.lat.value.toString()
                        )
                    } catch (e: Exception) {
                        Log.d("inputFormatter", "input was $input and caused exception ${e.cause}")
                    }
                },
                Modifier
                    .width(130.dp)
                    .height(58.dp),
                textStyle = TextStyle(fontSize = 18.sp, color = firstButton50),
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
                label = { Text("Latitude", color = firstButton50) },
                singleLine = true,
            )
            Spacer(modifier = Modifier.width(50.dp))
            OutlinedTextField(
                value = String.format(
                    "%.${showDecimals}f",
                    lon
                ), // viser lat, verdien som maks 5 desimaler
                onValueChange = { input ->
                    try {
                        mapViewModel.lon.value = formatNewValue(
                            input,
                            4,
                            5,
                            highestInput = 180.0,
                            lowestInput = -180.0,
                            oldValue = mapViewModel.lon.value.toString()
                        )
                    } catch (e: Exception) {
                        Log.d(
                            "inputFormatter",
                            "input was $input and caused exception ${e.message}"
                        )
                    }

                },
                Modifier
                    .width(130.dp)
                    .height(58.dp),

                textStyle = TextStyle(fontSize = 18.sp, color = firstButton50),
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
                label = { Text("Longitude", color = firstButton50) },
                singleLine = true
            )

        }
        Spacer(modifier = Modifier.height(20.dp))
        Row {


            OutlinedButton(modifier = Modifier.width(155.dp),
                border = BorderStroke(2.dp, firstButton0),
                onClick = {
                    controller?.hide()
                    mapViewModel.lat.value = lat
                    mapViewModel.lon.value = lon
                    mapViewModel.updateCamera(lat, lon)
                    scope.launch {
                        onEvent(FavoriteEvent.ShowDialog)
                    }

                }) {

                Icon(
                    modifier = Modifier.size(15.dp),
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = firstButton50
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text("Add favorite", color = firstButton50, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.width(25.dp))
            Button(
                modifier = Modifier.width(155.dp),
                colors = ButtonColors(
                    containerColor = firstButton0,
                    contentColor = firstButton100,
                    disabledContainerColor = firstButton0,
                    disabledContentColor = firstButton100
                ),
                onClick = {
                    controller?.hide()
                    homeScreenViewModel.getWeatherByCord(lat, lon)


                    scope.launch {
                        delay(200)
                        scaffoldState.bottomSheetState.expand()
                        getAirspace()
                    }

                }) {
                Text("Get weather data", fontSize = 13.sp)
            }


        }

    }
}