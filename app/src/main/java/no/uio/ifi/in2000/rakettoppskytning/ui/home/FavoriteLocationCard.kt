package no.uio.ifi.in2000.rakettoppskytning.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Favorite
import no.uio.ifi.in2000.rakettoppskytning.ui.home.map.MapViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.favoriteCard0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.favoriteCard100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.favoriteCard50
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.iconButton50

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteLocationCard(
    mapViewModel: MapViewModel,
    homeScreenViewModel: HomeScreenViewModel,
    favorite: Favorite,
    controller: SoftwareKeyboardController?,
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState
){
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
        border = BorderStroke(1.dp, color = iconButton50),
        onClick = {
            mapViewModel.lat.value = favorite.lat.toDouble()
            mapViewModel.lon.value = favorite.lon.toDouble()

            controller?.hide()
            homeScreenViewModel.getWeatherByPos(favorite.lat.toDouble(), favorite.lon.toDouble())


            scope.launch {
                delay(200)
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
                Text(
                    favorite.name,
                    fontSize = 18.sp,
                    color = favoriteCard100
                )

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
                        homeScreenViewModel.deleteFavoriteLocation(favorite.name, favorite.lat, favorite.lon)
                    }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete favorite",
                        tint = favoriteCard100.copy(0.7F)
                    )
                }
            }
        }
    }
}