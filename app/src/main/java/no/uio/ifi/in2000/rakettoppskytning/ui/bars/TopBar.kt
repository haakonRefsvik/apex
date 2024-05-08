package no.uio.ifi.in2000.rakettoppskytning.ui.bars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(navController: NavController) {
    androidx.compose.material3.TopAppBar(
        title = {}, modifier = Modifier
            .background(Color.Transparent)
            .height(0.dp)

    )
}