package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.maps.MapboxExperimental
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Favorite
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteEvent
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteState
import no.uio.ifi.in2000.rakettoppskytning.ui.favorite.AddFavoriteDialog

fun formatNewValue(input: String): Double {
    val onlyDigitsAndDot = input.filter { it.isDigit() || it == '.' || it == '-' }

    val decimalParts = onlyDigitsAndDot.split(".")
    val integerPart = decimalParts.getOrNull(0) ?: ""

    var formattedIntegerValue = integerPart

    while (formattedIntegerValue.length > 2){
        formattedIntegerValue = formattedIntegerValue.dropLast(1)
    }

    val decimalPart = if (decimalParts.size > 1) {
        "." + decimalParts[1]  // Reconstruct the decimal part, if present
    } else {
        ""
    }

    val r = (formattedIntegerValue + decimalPart)
    return (r).toDouble()
}


/** The inputfield where you can search for the weather at a spesific lat/lon */
@OptIn(MapboxExperimental::class, ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
<<<<<<< HEAD
fun InputField(homeScreenViewModel: HomeScreenViewModel,
               state: FavoriteState,
               onEvent: (FavoriteEvent) -> Unit,
               tappedFavorite: Boolean = false,
               favorite: Favorite){

=======
fun InputField(homeScreenViewModel: HomeScreenViewModel){
    val showDecimals = 5
    val lat by homeScreenViewModel.lat
>>>>>>> 2b689ba194ed9e6e0e4fad29f7098bd88d208019
    val lon by homeScreenViewModel.lon
    val lat by homeScreenViewModel.lat

    if (tappedFavorite) {
        homeScreenViewModel.lat.value = favorite.lat.toDouble()
        homeScreenViewModel.lat.value = favorite.lon.toDouble()
    }

    val controller = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val scope = rememberCoroutineScope()
    val scaffoldState by homeScreenViewModel.bottomSheetScaffoldState

    Row {
        OutlinedTextField(
            value = String.format("%.${showDecimals}f", lat), // viser lat, verdien som maks 5 desimaler
            onValueChange = {input ->
                homeScreenViewModel.lat.value = formatNewValue(input)
            },
            Modifier
                .width(130.dp)
                .height(58.dp),
            textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
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
            label = { Text("Latitude") },
            singleLine = true,
        )
        Spacer(modifier = Modifier.width(50.dp))
        OutlinedTextField(
            value = String.format("%.${showDecimals}f", lon), // viser lat, verdien som maks 5 desimaler
            onValueChange = { input ->
                homeScreenViewModel.lon.value = formatNewValue(input)
            },

            Modifier
                .width(130.dp)
                .height(58.dp),

            textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
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
            label = { Text("Longitude") },
            singleLine = true
        )

    }
    Spacer(modifier = Modifier.height(20.dp))
    Row {
        OutlinedButton(modifier = Modifier.width(155.dp), onClick = {
            controller?.hide()
            homeScreenViewModel.getVerticalProfileByCord(lat, lon)
            onEvent(FavoriteEvent.ShowDialog)
            //TODO: HER SKAL POSISJONEN TIL KARTET OPPDATERES
            scope.launch {
                scaffoldState.bottomSheetState.expand()
            }
        }) {
            if (state.isAddingFavorite) {
                AddFavoriteDialog(state = state, onEvent = onEvent, lat = lat, lon = lon)
            }
            Text("Legg til favoritter")
        }
        Spacer(modifier = Modifier.width(25.dp))
        Button(modifier = Modifier.width(155.dp), onClick = {
            controller?.hide()
            homeScreenViewModel.getForecastByCord(lat, lon)
            homeScreenViewModel.getVerticalProfileByCord(lat, lon)
            //TODO: HER SKAL POSISJONEN TIL KARTET OPPDATERES

            scope.launch {
                scaffoldState.bottomSheetState.expand()
            }
        }) {
            Text("Hent værdata")
        }
        Spacer(modifier = Modifier.height(70.dp))

    }

}