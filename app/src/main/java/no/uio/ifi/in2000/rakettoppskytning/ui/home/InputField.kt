package no.uio.ifi.in2000.rakettoppskytning.ui.home


import android.content.Context
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CardColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.maps.MapboxExperimental
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Favorite
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteEvent
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteState
import no.uio.ifi.in2000.rakettoppskytning.ui.home.favorite.AddFavoriteDialog
import no.uio.ifi.in2000.rakettoppskytning.ui.settings.formatNewValue
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.StatusColor
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.favoriteCard0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.favoriteCard100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.favoriteCard50
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.firstButton0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.firstButton100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.firstButton50
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main50

/*
fun formatNewValue(input: String): Double {
    val onlyDigitsAndDot = input.filter { it.isDigit() || it == '.' || it == '-' }

    val decimalParts = onlyDigitsAndDot.split(".")
    val integerPart = decimalParts.getOrNull(0) ?: ""

    var formattedIntegerValue = integerPart

    while (formattedIntegerValue.length > 2) {
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

 */

/** The inputfield where you can search for the weather at a spesific lat/lon */

@OptIn(MapboxExperimental::class, ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InputField(
    homeScreenViewModel: HomeScreenViewModel,
    mapViewModel: MapViewModel,
    context: Context
) {

    val showDecimals = 5

    val lat by mapViewModel.lat
    val lon by mapViewModel.lon

    val favoriteLocations by homeScreenViewModel.favoriteUiState.collectAsState()


    LaunchedEffect(Unit) {
        homeScreenViewModel.getFavoriteLocations()
    }



    val controller = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val scope = rememberCoroutineScope()
    val scaffoldState by homeScreenViewModel.bottomSheetScaffoldState

    Log.d("Før addingFav: ", "lat: ${lat} og lon: ${lon}")

    var isAddingFavorite by remember{ mutableStateOf(false) }
    var clickDelete by remember {
        mutableStateOf(false)
    }


    if (isAddingFavorite) {
        Log.d("addingFav: ", "lat: ${lat} og lon: ${lon}")
        AddFavoriteDialog(
            homeScreenViewModel = homeScreenViewModel,
            lat = lat,
            lon = lon,
            context = context,
            isAddingFavorite = isAddingFavorite, // Pass the state to AddFavoriteDialog
            onDismiss = { isAddingFavorite = false } // Callback to dismiss the dialog
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = main100),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Row {
            OutlinedTextField(
                value = String.format(
                    "%.${showDecimals}f",
                    lat
                ), // viser lat, verdien som maks 5 desimaler
                onValueChange = { input ->
                    try {
                        mapViewModel.lat.value = formatNewValue(
                            input,
                            2,
                            5,
                            highestInput = 90.0,
                            lowestInput = -90.0,
                            oldValue = mapViewModel.lat.value.toString()
                        )
                    }catch (e: Exception){
                        Log.d("inputFormatter", "input was $input and caused exception ${e.cause}")
                    }
                },
                Modifier
                    .width(130.dp)
                    .height(58.dp),
                textStyle = TextStyle(fontSize = 18.sp, color = firstButton50),
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
                label = { Text("Latitude", color = firstButton50) },
                singleLine = true,
            )
            Spacer(modifier = Modifier.width(50.dp))
            OutlinedTextField(
                value = String.format(
                    "%.${showDecimals}f",
                    lon
                ), // viser lat, verdien som maks 5 desimaler
                onValueChange = { input ->
                    try {
                        mapViewModel.lon.value = formatNewValue(
                            input,
                            4,
                            5,
                            highestInput = 180.0,
                            lowestInput = -180.0,
                            oldValue = mapViewModel.lon.value.toString()
                        )
                    }catch (e: Exception){
                        Log.d("inputFormatter", "input was $input and caused exception ${e.message}")
                    }

                },
                Modifier
                    .width(130.dp)
                    .height(58.dp),

                textStyle = TextStyle(fontSize = 18.sp, color = firstButton50),
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
                label = { Text("Longitude", color = firstButton50) },
                singleLine = true
            )

        }
        Spacer(modifier = Modifier.height(20.dp))
        Row {
            Log.d("moveCam -1: ", "lat: ${lat} og lon: ${lon}")


            OutlinedButton(modifier = Modifier.width(155.dp),
                border = BorderStroke(2.dp, firstButton0),
                onClick = {
                    controller?.hide()
                    mapViewModel.lat.value = lat
                    mapViewModel.lon.value = lon

                    mapViewModel.updateCamera(lat, lon)
                    scope.launch {
                        isAddingFavorite = true
                    }

                }) {

                Icon(
                    modifier = Modifier.size(15.dp),
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = firstButton50
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text("Add favorite", color = firstButton50)
            }
            Spacer(modifier = Modifier.width(25.dp))
            Button(
                modifier = Modifier.width(155.dp),
                colors = ButtonColors(
                    containerColor = firstButton0,
                    contentColor = firstButton100,
                    disabledContainerColor = firstButton0,
                    disabledContentColor = firstButton100
                ),
                onClick = {
                    controller?.hide()
                    homeScreenViewModel.getWeatherByCord(lat, lon)


                    scope.launch {
                        delay(1000)
                        scaffoldState.bottomSheetState.expand()
                    }
                }) {
                Text("Get weatherdata")
            }
            Spacer(modifier = Modifier.height(70.dp))

        }
        Spacer(modifier = Modifier.height(2.5.dp))
        if (favoriteLocations.favorites.isNotEmpty()) {
            Row(modifier = Modifier.width(340.dp)) {
                if (favoriteLocations.favorites.size == 1) {
                    Text("Favorite location:", fontSize = 14.sp, color = main50)

                } else {
                    Text("Favorite locations:", fontSize = 14.sp, color = main50)
                }


            }

        }
        Spacer(modifier = Modifier.height(2.5.dp))
        LazyRow(
            modifier = Modifier.width(340.dp)
        )
        {

            favoriteLocations.favorites.reversed().forEach { favorite ->
                item {
                    OutlinedCard(
                        modifier = Modifier
                            .height(55.dp)
                            .width(200.dp),
                        colors = CardColors(
                            containerColor = favoriteCard50,
                            contentColor = favoriteCard0,
                            disabledContentColor = favoriteCard50,
                            disabledContainerColor = favoriteCard0
                        ),
                        border = BorderStroke(1.dp, color = favoriteCard100),
                        onClick = {
                            mapViewModel.lat.value = favorite.lat.toDouble()
                            mapViewModel.lon.value = favorite.lon.toDouble()

                            controller?.hide()
                            homeScreenViewModel.getWeatherByCord(lat, lon)


                            scope.launch {
                                delay(1000)
                                scaffoldState.bottomSheetState.expand()
                            }

                        }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize()


                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(175.dp),
                                verticalAlignment = Alignment.CenterVertically

                            ) {
                                Spacer(modifier = Modifier.width(10.dp))
                                Icon(
                                    modifier = Modifier.size(25.dp),
                                    imageVector = Icons.Default.Place,
                                    contentDescription = "Location",
                                    tint = Color(216, 64, 64, 255)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(favorite.name, fontSize = 18.sp, color = favoriteCard100)

                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            )

                            {
                                IconButton(modifier = Modifier
                                    .size(30.dp)
                                    .padding(end = 5.dp),
                                    onClick = {
                                        Log.d("Favorite: ", "${favorite.name}, ${favorite.lat}, ${favorite.lon}" )
                                        homeScreenViewModel.deleteFavoriteLocation(favorite.name, favorite.lat, favorite.lon)
                                    }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Delete favorite",
                                        tint = favoriteCard100

                                    )

                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                }
            }


        }
    }
}