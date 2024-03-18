package no.uio.ifi.in2000.rakettoppskytning.ui.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteEvent
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteState
import no.uio.ifi.in2000.rakettoppskytning.ui.home.MapViewModel

//Lag funksjonen slik at den ikke leser inn mer enn 1 gang per lokasjon
@Composable
fun AddFavoriteDialog(
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
                    state.favorites.forEach() {
                        if ((it.lat.toDouble() != lat) && (it.lon.toDouble() != lon)) {
                            onEvent(FavoriteEvent.SaveFavorite)
                        }

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