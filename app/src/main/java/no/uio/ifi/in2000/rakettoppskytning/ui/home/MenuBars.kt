package no.uio.ifi.in2000.rakettoppskytning.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.sharp.LocationOn
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.data.navigation.Routes
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main50
import okhttp3.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomAppBar(navController: NavController, homeScreenViewModel: HomeScreenViewModel) {
    val scope = rememberCoroutineScope()
    BottomAppBar(
        containerColor = main50,
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
                .background(color = main50),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(modifier = Modifier.sizeIn(maxWidth = 38.dp), onClick = {
                navController.navigate(Routes.favCards)
            }) {
                Icon(
                    Icons.Default.Favorite,
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = "Favorite",
                    tint = main0,
                )
            }

            Spacer(modifier = Modifier.widthIn(110.dp))
            IconButton(onClick = {
                scope.launch { homeScreenViewModel.scaffold.bottomSheetState.partialExpand() }
            }) {
                Icon(
                    Icons.Sharp.LocationOn,
                    modifier = Modifier.size(40.dp),
                    contentDescription = "Location",
                    tint = main100
                )
            }

            Spacer(modifier = Modifier.width(110.dp))
            IconButton(onClick = { navController.navigate(Routes.settings) }) {
                Icon(
                    Icons.Sharp.Settings,
                    modifier = Modifier.size(40.dp),
                    contentDescription = "Settings",
                    tint = main0
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(navController: NavController) {
    TopAppBar(
        title = {}, modifier = Modifier
            .background(Color.Transparent)
            .height(0.dp)

    )
}