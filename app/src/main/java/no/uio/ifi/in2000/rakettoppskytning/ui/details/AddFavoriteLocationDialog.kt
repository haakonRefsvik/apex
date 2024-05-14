package no.uio.ifi.in2000.rakettoppskytning.ui.details

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.home.AddFavoriteDialogCorrect

@Composable
fun AddFavoriteLocationDialog(
    homeScreenViewModel: HomeScreenViewModel,
    lat: Double,
    lon: Double,
    context: Context,
    isAddingFavorite: Boolean,
    onDismiss: () -> Unit,
) {

    val favoriteLocations by homeScreenViewModel.favoriteUiState.collectAsState()

    val isLocationFavorited =
        favoriteLocations.favorites.any { it.lat.toDouble() == lat && it.lon.toDouble() == lon }

    if (!isLocationFavorited) {
        AddFavoriteDialogCorrect(
            homeScreenViewModel = homeScreenViewModel,
            lat = lat,
            lon = lon,
            context = context,
            isAddingFavorite = isAddingFavorite,
            onDismiss = onDismiss,
            displayText = "This location does not have a name. Do you want to give it a name?",
            dismissText = "No"
        )
    }
    else{
        onDismiss()
    }
}

