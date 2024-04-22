package no.uio.ifi.in2000.rakettoppskytning.ui.home.favorite

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteEvent
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteState
import no.uio.ifi.in2000.rakettoppskytning.ui.home.MapViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.favorite0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.favorite100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main100

//Lag funksjonen slik at den ikke leser inn mer enn 1 gang per lokasjon
@OptIn(MapboxExperimental::class)

@Composable
fun AddFavoriteDialogCorrect(
    state: FavoriteState,
    onEvent: (FavoriteEvent) -> Unit,
    lat: Double,
    lon: Double,
    mapViewModel: MapViewModel, context: Context
) {
    val controller = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    var inputName by remember { mutableStateOf(state.name) }
    var isNameAlreadyUsed by remember { mutableStateOf(false) }

    val duration = Toast.LENGTH_LONG

    val toast = Toast.makeText(context, "Added $inputName to favorites", duration) // in Activity

    AlertDialog(
        containerColor = main100,
        title = {
            Text(text = "Legg til favoritt", color = favorite100)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OutlinedTextField(
                    value = inputName, // viser lat, verdien som maks 5 desimaler
                    onValueChange = {
                        inputName = it
                        isNameAlreadyUsed = state.favorites.any { favorite -> favorite.name == it }
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
                    modifier = Modifier.focusRequester(focusRequester),
                    colors = TextFieldColors(
                        focusedTextColor = favorite100,
                        cursorColor = favorite100,
                        disabledContainerColor = favorite100,
                        disabledIndicatorColor = favorite100,
                        disabledLabelColor = favorite100,
                        disabledLeadingIconColor = favorite100,
                        disabledPlaceholderColor = favorite100,
                        disabledPrefixColor = favorite100,
                        disabledSuffixColor = favorite100,
                        disabledSupportingTextColor = favorite100,
                        disabledTextColor = favorite100,
                        disabledTrailingIconColor = favorite100,
                        errorContainerColor = favorite100,
                        errorCursorColor = favorite100,
                        errorIndicatorColor = favorite100,
                        errorLabelColor = favorite100,
                        errorLeadingIconColor = favorite100,
                        errorPlaceholderColor = favorite100,
                        errorPrefixColor = favorite100,
                        errorSuffixColor = favorite100,
                        errorSupportingTextColor = favorite100,
                        errorTextColor = favorite100,
                        errorTrailingIconColor = favorite100,
                        focusedContainerColor = main100,
                        focusedIndicatorColor = favorite100,
                        focusedLabelColor = favorite100,
                        focusedLeadingIconColor = favorite100,
                        focusedPlaceholderColor = favorite100,
                        focusedPrefixColor = favorite100,
                        focusedSuffixColor = favorite100,
                        focusedSupportingTextColor = favorite100,
                        focusedTrailingIconColor = favorite100,
                        textSelectionColors = TextSelectionColors(favorite100, favorite100),
                        unfocusedContainerColor = main100,
                        unfocusedIndicatorColor = favorite100,
                        unfocusedLabelColor = favorite100,
                        unfocusedLeadingIconColor = favorite100,
                        unfocusedPlaceholderColor = favorite100,
                        unfocusedPrefixColor = favorite100,
                        unfocusedSuffixColor = favorite100,
                        unfocusedSupportingTextColor = favorite100,
                        unfocusedTextColor = favorite100,
                        unfocusedTrailingIconColor = favorite100
                    )
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
                        CoroutineScope(Dispatchers.Default).launch {
                            onEvent(FavoriteEvent.SetName(inputName))
                            onEvent(FavoriteEvent.SetLat(lat.toString()))
                            onEvent(FavoriteEvent.SetLon(lon.toString()))
                            delay(500L)
                            onEvent(FavoriteEvent.SaveFavorite)
                            toast.show()
                        }
                    }
                }

            ) {
                Text("Confirm", color = favorite100)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onEvent(FavoriteEvent.HideDialog)
                }
            ) {
                Text("Dismiss", color = Color.Red)
            }
        }
    )
}


@OptIn(MapboxExperimental::class)
@Composable
fun AddFavoriteDialogError(
    state: FavoriteState,
    onEvent: (FavoriteEvent) -> Unit,
    lat: Double,
    lon: Double,
    mapViewModel: MapViewModel
) {
    val favorite = state.favorites.find { it.lat.toDouble() == lat && it.lon.toDouble() == lon }
    AlertDialog(
        containerColor = main100,
        icon = {
            androidx.compose.material3.Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning Icon",
                tint = Color.Red
            )
        },
        title = {
            Text(text = "Legg til favoritt", color = favorite100)
        },
        text = {
            if (favorite != null) {
                Text(
                    "Denne lokasjonen er allerede lagret under navnet ${favorite.name}",
                    color = favorite100
                )
            }
        },
        onDismissRequest = {},
        confirmButton = {
            TextButton(
                onClick = {
                    onEvent(FavoriteEvent.HideDialog)
                }
            ) {
                Text("OK", color = favorite100)
            }
        }
    )
}

@Composable
fun AddFavoriteDialog(
    state: FavoriteState,
    onEvent: (FavoriteEvent) -> Unit,
    lat: Double,
    lon: Double,
    mapViewModel: MapViewModel,
    context: Context
) {

    val isLocationFavorited =
        state.favorites.any { it.lat.toDouble() == lat && it.lon.toDouble() == lon }

    if (isLocationFavorited) {
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
            mapViewModel = mapViewModel,
            context = context
        )
    }
}
