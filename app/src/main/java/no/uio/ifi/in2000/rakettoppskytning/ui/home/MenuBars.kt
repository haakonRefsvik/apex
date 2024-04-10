package no.uio.ifi.in2000.rakettoppskytning.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.LocationOn
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.primaryDark
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.secondaryDark

@Composable
fun BottomAppBar(navController: NavController){
    BottomAppBar {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(color = primaryDark),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    Icons.Sharp.LocationOn,
                    modifier = Modifier.size(40.dp),
                    contentDescription = "Location",
                    tint = secondaryDark
                )
            }
            Spacer(modifier = Modifier.width(94.dp))
            IconButton(onClick = {/* TODO */}) {
                Image(
                    painter = painterResource(R.drawable.rakket),
                    contentDescription = "Rakket"
                )
            }
            Spacer(modifier = Modifier.width(95.dp))
            IconButton(onClick = { navController.navigate("ThresholdScreen") }) {
                Icon(
                    Icons.Sharp.Settings,
                    modifier = Modifier.size(40.dp),
                    contentDescription = "Settings",
                    tint = secondaryDark
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(navController: NavController){
    TopAppBar(
        title = {}, modifier = Modifier
            .background(Color.Transparent)
            .height(0.dp)

    )
}