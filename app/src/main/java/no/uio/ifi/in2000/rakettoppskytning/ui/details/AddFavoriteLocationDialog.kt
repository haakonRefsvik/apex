package no.uio.ifi.in2000.rakettoppskytning.ui.details

import android.content.Context
import androidx.compose.runtime.Composable
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteEvent
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteState
import no.uio.ifi.in2000.rakettoppskytning.ui.home.favorite.AddFavoriteDialogCorrect

@Composable
fun AddFavoriteLocationDialog(
    state: FavoriteState,
    onEvent: (FavoriteEvent) -> Unit,
    lat: Double,
    lon: Double,
    context: Context
) {

    val isLocationFavorited =
        state.favorites.any { it.lat.toDouble() == lat && it.lon.toDouble() == lon }

    if (!isLocationFavorited) {
        AddFavoriteDialogCorrect(
            state = state,
            onEvent = onEvent,
            lat = lat,
            lon = lon,
            context = context,
            displayText = "This location does not have a name. Do you want to give it a name?",
            dismissText = "No"
        )
    }
    else{
        onEvent(FavoriteEvent.HideDialog)
    }
}
