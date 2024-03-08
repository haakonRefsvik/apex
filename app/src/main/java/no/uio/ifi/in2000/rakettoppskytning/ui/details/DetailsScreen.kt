package no.uio.ifi.in2000.rakettoppskytning.ui.details

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.Details

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    navController: NavHostController,

    backStackEntry: Details?,

    ){

    val detailsNavn = listOf<String>("airPressureAtSeaLevel", "airTemperature", "airTemperaturePercentile10","airTemperaturePercentile90",
        "cloudAreaFraction","cloudAreaFractionHigh","cloudAreaFractionLow","cloudAreaFractionMedium","dewPointTemperature","fogAreaFraction","relativeHumidity",
        "ultravioletIndexClearSky","windFromDirection","windSpeed","windSpeedOfGust","windSpeedPercentile10","windSpeedPercentile90")
    val details:List<Details> = if (backStackEntry != null) {
        listOf(backStackEntry)

    }
    else{
        listOf()
    }
    var index: Int
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },

        topBar = {
            TopAppBar(
                title = {

                    ClickableText(


                        text = AnnotatedString(
                            text = "ToppAppBar",
                            spanStyle = SpanStyle(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 15.sp
                            )
                        ),
                        onClick = { navController.navigateUp()},

                        )

                },
                navigationIcon = {

                },
            )
        },
        bottomBar = {
            BottomAppBar() {
                Text(text = "BottomAppBar")



            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 120.dp),
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(),




        ) {index = 0
             details.forEach{
                 it.forEach {
                     item {
                         ElevatedCard(
                             modifier = Modifier
                                 .height(100.dp)
                                 .width(340.dp))
                         {
                             Text(detailsNavn[index++])
                             Text(it.toString())




                         }

                     }

                 }

             }
            index = 0


            }
        }

}