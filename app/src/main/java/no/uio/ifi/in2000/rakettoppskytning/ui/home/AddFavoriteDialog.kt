package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.maps.MapboxExperimental
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.favorite100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main100

//Lag funksjonen slik at den ikke leser inn mer enn 1 gang per lokasjon
@OptIn(MapboxExperimental::class)

@Composable
fun AddFavoriteDialogCorrect(
    homeScreenViewModel: HomeScreenViewModel,
    lat: Double,
    lon: Double,
    context: Context,
    isAddingFavorite: Boolean,
    onDismiss: () -> Unit,
    displayText: String = "Add location to favorite",
    dismissText: String = "Dismiss"
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    var inputName by remember { mutableStateOf("") }
    var isNameAlreadyUsed by remember { mutableStateOf(false) }

    val favoriteLocations by homeScreenViewModel.favoriteUiState.collectAsState()

    val duration = Toast.LENGTH_LONG

    val toast = Toast.makeText(context, "Added $inputName to favorites", duration) // in Activity

    if(isAddingFavorite) {
        AlertDialog(
            containerColor = main100,
            title = {
                Text(text = "Add favorite", color = favorite100)
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = displayText, color = favorite100.copy(0.7F))
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = inputName, // viser lat, verdien som maks 5 desimaler
                        onValueChange = {
                            inputName = it
                            isNameAlreadyUsed = favoriteLocations.favorites.any { favorite -> favorite.name == it }
                        },

                        textStyle = TextStyle(fontSize = 18.sp),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (!isNameAlreadyUsed && inputName != "") {
                                    CoroutineScope(Dispatchers.Default).launch {
                                        homeScreenViewModel.addFavorite(
                                            inputName,
                                            lat.toString(),
                                            lon.toString()
                                        )
                                        toast.show()
                                        onDismiss()
                                    }
                                }
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
                        Text("This name is already in use", color = favorite100.copy(0.7F))
                    }
                }
            },
            onDismissRequest = {
                inputName = ""
                onDismiss()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (!isNameAlreadyUsed && inputName != "") {
                            CoroutineScope(Dispatchers.Default).launch {
                                homeScreenViewModel.addFavorite(inputName, lat.toString(), lon.toString())
                                toast.show()
                                onDismiss()
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
                        onDismiss()
                    }
                ) {
                    Text(dismissText, color = favorite100.copy(0.7F))
                }
            }
        )

    }

}


@Composable
fun AddFavoriteDialogError(
    homeScreenViewModel: HomeScreenViewModel,
    lat: Double,
    lon: Double,
    onDismiss: () -> Unit
) {

    val favoriteLocations by homeScreenViewModel.favoriteUiState.collectAsState()

    val favorite = favoriteLocations.favorites.find { it.lat.toDouble() == lat && it.lon.toDouble() == lon }
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
            Text(text = "Add favorite", color = favorite100)
        },
        text = {
            if (favorite != null) {
                Text(
                    "This location is already saved under the name ${favorite.name}",
                    color = favorite100
                )
            }
        },
        onDismissRequest = { onDismiss()},
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("OK", color = favorite100)
            }
        }
    )
}

@Composable
fun AddFavoriteDialog(
    homeScreenViewModel: HomeScreenViewModel,
    lat: Double,
    lon: Double,
    context: Context,
    isAddingFavorite: Boolean,
    onDismiss: () -> Unit
) {

    val favoriteLocations by homeScreenViewModel.favoriteUiState.collectAsState()

    val isLocationFavorited =
        favoriteLocations.favorites.any { it.lat.toDouble() == lat && it.lon.toDouble() == lon }

    if (isLocationFavorited) {
        AddFavoriteDialogError(
            homeScreenViewModel = homeScreenViewModel,
            lat = lat,
            lon = lon,
            onDismiss = onDismiss
        )
    } else {
        AddFavoriteDialogCorrect(
            homeScreenViewModel = homeScreenViewModel,
            lat = lat,
            lon = lon,
            context = context,
            isAddingFavorite = isAddingFavorite,
            onDismiss = onDismiss
        )
    }
}
