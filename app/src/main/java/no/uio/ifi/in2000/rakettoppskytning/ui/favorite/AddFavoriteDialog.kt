package no.uio.ifi.in2000.rakettoppskytning.ui.favorite

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
import com.mapbox.maps.MapboxExperimental
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteEvent
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteState
import no.uio.ifi.in2000.rakettoppskytning.ui.home.MapViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.favorite0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.favorite100

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
        containerColor = favorite100,
        title = {
            Text(text = "Legg til favoritt", color = favorite0)
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
                    label = { Text("Name", color = favorite0) },
                    singleLine = true,
                    modifier = Modifier.focusRequester(focusRequester),
                    colors = TextFieldColors(
                        focusedTextColor = favorite0,
                        cursorColor = favorite0,
                        disabledContainerColor = favorite0,
                        disabledIndicatorColor = favorite0,
                        disabledLabelColor = favorite0,
                        disabledLeadingIconColor = favorite0,
                        disabledPlaceholderColor = favorite0,
                        disabledPrefixColor = favorite0,
                        disabledSuffixColor = favorite0,
                        disabledSupportingTextColor = favorite0,
                        disabledTextColor = favorite0,
                        disabledTrailingIconColor = favorite0,
                        errorContainerColor = favorite100,
                        errorCursorColor = favorite0,
                        errorIndicatorColor = favorite0,
                        errorLabelColor = favorite0,
                        errorLeadingIconColor = favorite0,
                        errorPlaceholderColor = favorite0,
                        errorPrefixColor = favorite0,
                        errorSuffixColor = favorite0,
                        errorSupportingTextColor = favorite0,
                        errorTextColor = favorite0,
                        errorTrailingIconColor = favorite0,
                        focusedContainerColor = favorite100,
                        focusedIndicatorColor = favorite0,
                        focusedLabelColor = favorite0,
                        focusedLeadingIconColor = favorite0,
                        focusedPlaceholderColor = favorite0,
                        focusedPrefixColor = favorite0,
                        focusedSuffixColor = favorite0,
                        focusedSupportingTextColor = favorite0,
                        focusedTrailingIconColor = favorite0,
                        textSelectionColors = TextSelectionColors(favorite0, favorite0),
                        unfocusedContainerColor = favorite100,
                        unfocusedIndicatorColor = favorite0,
                        unfocusedLabelColor = favorite0,
                        unfocusedLeadingIconColor = favorite0,
                        unfocusedPlaceholderColor = favorite0,
                        unfocusedPrefixColor = favorite0,
                        unfocusedSuffixColor = favorite0,
                        unfocusedSupportingTextColor = favorite0,
                        unfocusedTextColor = favorite0,
                        unfocusedTrailingIconColor = favorite0)
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
                        }
                    }
                }

            ) {
                Text("Confirm", color = favorite0)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onEvent(FavoriteEvent.HideDialog)
                }
            ) {
                Text("Dismiss", color = favorite0)
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
        containerColor = favorite100,
        icon = {
            androidx.compose.material3.Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning Icon",
                tint = Color.Red
            )
        },
        title = {
            Text(text = "Legg til favoritt", color = favorite0)
        },
        text = {
            if (favorite != null) {
                Text("Denne lokasjonen er allerede lagret under navnet ${favorite.name}", color = favorite0)
            }
        },
        onDismissRequest = {},
        confirmButton = {
            TextButton(
                onClick = {
                    onEvent(FavoriteEvent.HideDialog)
                }
            ) {
                Text("OK", color = favorite0)
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

    val isLocationFavorited = state.favorites.any { it.lat.toDouble() == lat && it.lon.toDouble() == lon }

    if (isLocationFavorited) {
        AddFavoriteDialogError(state = state, onEvent = onEvent, lat = lat, lon = lon, mapViewModel = mapViewModel)
    }
    else {
        AddFavoriteDialogCorrect(state = state, onEvent = onEvent, lat = lat, lon = lon, mapViewModel = mapViewModel)
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