package no.uio.ifi.in2000.rakettoppskytning.ui.favorite

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.dsl.cameraOptions
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteEvent
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteState
import no.uio.ifi.in2000.rakettoppskytning.ui.home.MapViewModel

//Lag funksjonen slik at den ikke leser inn mer enn 1 gang per lokasjon
@OptIn(MapboxExperimental::class)

@Composable
fun AddFavoriteDialogCorrect(
    state: FavoriteState,
    onEvent: (FavoriteEvent) -> Unit,
    lat: Double,
    lon: Double,
    mapViewModel: MapViewModel
) {
    val controller = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    var inputName by remember { mutableStateOf(state.name) }
    var isNameAlreadyUsed by remember { mutableStateOf(false) }

    AlertDialog(
        title = {
            Text(text = "Legg til favoritt")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OutlinedTextField(
                    value = inputName, // viser lat, verdien som maks 5 desimaler
                    onValueChange = {
                        inputName = it
                    },

                    textStyle = TextStyle(fontSize = 18.sp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            controller?.hide()
                            focusManager.clearFocus()
                        }
                    ),
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.focusRequester(focusRequester)
                )
                if (isNameAlreadyUsed) {
                    Text("Dette navnet er allerede i bruk", color = Color.Red)
                }
            }
        },
        onDismissRequest = {
            onEvent(FavoriteEvent.HideDialog)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (!isNameAlreadyUsed) {
                        onEvent(FavoriteEvent.SaveFavorite)
                    }
                }

            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onEvent(FavoriteEvent.HideDialog)
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}


@OptIn(MapboxExperimental::class)
@Composable
fun AddFavoriteDialogError(state: FavoriteState,
                           onEvent: (FavoriteEvent) -> Unit,
                           lat: Double,
                           lon: Double,
                           mapViewModel: MapViewModel
) {
    val favorite = state.favorites.find { it.lat.toDouble() == lat && it.lon.toDouble() == lon}
    AlertDialog(
        icon = { androidx.compose.material3.Icon(imageVector = Icons.Default.Warning, contentDescription = "Warning Icon", tint = Color.Red)},
        title = {
            Text(text = "Legg til favoritt")
        },
        text = {
            if (favorite != null) {
                Text("Denne lokasjonen er allerede lagret under navnet ${favorite.name}")
            }
        },
        onDismissRequest = {},
        confirmButton = {
            TextButton(
                onClick = {
                    onEvent(FavoriteEvent.HideDialog)
                }
            ) {
                Text("OK")
            }
        }
    )
}

@OptIn(MapboxExperimental::class)
@Composable
fun AddFavoriteDialog(
    state: FavoriteState,
    onEvent: (FavoriteEvent) -> Unit,
    lat: Double,
    lon: Double,
    mapViewModel: MapViewModel
) {
    val isLocationFavorited = remember(lat, lon) {
        mutableStateOf(state.favorites.any {
            it.lat.toDouble() == lat && it.lon.toDouble() == lon
        })
    }

    if (isLocationFavorited.value) {
        AddFavoriteDialogError(
            state = state,
            onEvent = onEvent,
            lat = lat,
            lon = lon,
            mapViewModel = mapViewModel
        )
    } else {
        AddFavoriteDialogCorrect(
            state = state,
            onEvent = onEvent,
            lat = lat,
            lon = lon,
            mapViewModel = mapViewModel
        )
    }
}

/*
@Composable
fun AddFavoriteDialogCorrect(
    state: FavoriteState,
    onEvent: (FavoriteEvent) -> Unit,
    lat: Double,
    lon: Double,
    mapViewModel: MapViewModel
) {

    val controller = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        title = {
            Text(text = "Legg til favoritt")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OutlinedTextField(
                    value = state.name, // viser lat, verdien som maks 5 desimaler
                    onValueChange = {
                        onEvent(FavoriteEvent.SetName(it))
                        onEvent(FavoriteEvent.SetLat(lat.toString()))
                        onEvent(FavoriteEvent.SetLon(lon.toString()))
                    },

                    textStyle = TextStyle(fontSize = 18.sp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            controller?.hide()
                            focusManager.clearFocus()
                        }
                    ),
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.focusRequester(focusRequester)
                )
            }
        },
        onDismissRequest = {
            onEvent(FavoriteEvent.HideDialog)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onEvent(FavoriteEvent.SaveFavorite)
                }

            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onEvent(FavoriteEvent.HideDialog)
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}
*/