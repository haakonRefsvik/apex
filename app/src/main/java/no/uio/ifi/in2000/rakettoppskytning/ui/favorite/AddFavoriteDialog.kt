package no.uio.ifi.in2000.rakettoppskytning.ui.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteEvent
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteState
import no.uio.ifi.in2000.rakettoppskytning.ui.home.MapViewModel

//Lag funksjonen slik at den ikke leser inn mer enn 1 gang per lokasjon
@Composable
fun AddFavoriteDialogCorrect(
    state: FavoriteState,
    onEvent: (FavoriteEvent) -> Unit,
    lat: Double,
    lon: Double
) {
    AlertDialog(
        title = {
            Text(text = "Add favorite")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {


                TextField(
                    value = state.name,
                    onValueChange = {
                        onEvent(FavoriteEvent.SetName(it))
                        onEvent(FavoriteEvent.SetLat(lat.toString()))
                        onEvent(FavoriteEvent.SetLon(lon.toString()))
                    },
                    placeholder = {
                        Text(text = "Name")
                    }
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

@Composable
fun AddFavoriteDialogError(state: FavoriteState,
                      onEvent: (FavoriteEvent) -> Unit,
                      lat: Double,
                      lon: Double) {
    val favorite = state.favorites.find { it.lat.toDouble() == lat && it.lon.toDouble() == lon}
    AlertDialog(
        icon = { androidx.compose.material3.Icon(imageVector = Icons.Default.Warning, contentDescription = "Warning Icon", tint = Color.Red)},
        title = {
            Text(text = "Add Favorite")
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

@Composable
fun AddFavoriteDialog(
    state: FavoriteState,
    onEvent: (FavoriteEvent) -> Unit,
    lat: Double,
    lon: Double
) {
    val isLocationFavorited = state.favorites.any { it.lat.toDouble() == lat && it.lon.toDouble() == lon }

    if (isLocationFavorited) {
        AddFavoriteDialogError(state = state, onEvent = onEvent, lat = lat, lon = lon)
    }
    else {
        AddFavoriteDialogCorrect(state = state, onEvent = onEvent, lat = lat, lon = lon)
    }
}



