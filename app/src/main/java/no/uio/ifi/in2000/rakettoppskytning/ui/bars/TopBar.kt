package no.uio.ifi.in2000.rakettoppskytning.ui.bars

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.settings0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.settings100

/**
 * This Kotlin function defines a TopAppBar with a navigation icon and title,
 * enabling navigation via a NavController in Jetpack Compose UI,
 * and applies theme-based color settings.
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController) {
    TopAppBar(
        colors = TopAppBarColors(settings100, settings100, settings0, settings0, settings0),
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "ArrowBack",
                    tint = settings0
                )
            }
        },
        title = {
            ClickableText(
                text = AnnotatedString(
                    text = "",
                    spanStyle = SpanStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 15.sp
                    )
                ),
                onClick = { navController.navigateUp() },
            )
        },
    )
}