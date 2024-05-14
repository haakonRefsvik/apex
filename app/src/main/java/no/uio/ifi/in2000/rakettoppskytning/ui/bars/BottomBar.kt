package no.uio.ifi.in2000.rakettoppskytning.ui.bars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.sharp.LocationOn
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.data.navigation.Routes
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.iconButton50
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main100

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBar(
    navController: NavController,
    homeScreenViewModel: HomeScreenViewModel,
    currentScreen: String,
) {
    val unChosenColor = iconButton50
    val chosenColor = main0
    val color = lerp(main100, Black, 0.2F)
    val scope = rememberCoroutineScope()
    BottomAppBar(
        containerColor = color,
        modifier = Modifier
            .shadow(
                10.dp,
                RectangleShape,
                false,
                DefaultShadowColor,
                DefaultShadowColor
            )
            .heightIn(max = 50.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(color),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier
                    .sizeIn(maxWidth = 38.dp),
                onClick = {
                    if (currentScreen != Routes.favCards) {
                        navController.navigate(Routes.favCards)
                    }
                }) {
                Icon(
                    Icons.Default.Favorite,
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = "Favorite",
                    tint =
                    if (currentScreen == Routes.favCards) {
                        chosenColor
                    } else {
                        unChosenColor
                    },
                )

            }

            IconButton(onClick = {
                if (currentScreen != Routes.home) {
                    navController.popBackStack(Routes.home, false)
                    scope.launch { homeScreenViewModel.scaffold.bottomSheetState.partialExpand() }
                } else {
                    scope.launch { homeScreenViewModel.scaffold.bottomSheetState.partialExpand() }
                }

            }) {
                Icon(
                    Icons.Sharp.LocationOn,
                    modifier = Modifier.size(40.dp),
                    contentDescription = "Location",
                    tint =
                    if (currentScreen == Routes.home) {
                        chosenColor
                    } else {
                        unChosenColor
                    }
                )
            }

            IconButton(onClick = {
                if (currentScreen != Routes.settings) {
                    navController.navigate(Routes.settings)
                }
            }) {
                Icon(
                    Icons.Sharp.Settings,
                    modifier = Modifier
                        .size(40.dp),
                    contentDescription = "Settings",
                    tint =
                    if (currentScreen == Routes.settings) {
                        chosenColor
                    } else {
                        unChosenColor
                    }
                )
            }
        }
    }
}