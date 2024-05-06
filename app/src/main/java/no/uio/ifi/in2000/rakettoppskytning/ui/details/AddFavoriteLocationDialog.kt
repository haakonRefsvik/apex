package no.uio.ifi.in2000.rakettoppskytning.ui.details

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteEvent
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteState
import no.uio.ifi.in2000.rakettoppskytning.ui.home.favorite.AddFavoriteDialogCorrect
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.favorite100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main100

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
