package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mapbox.maps.MapboxExperimental
import no.uio.ifi.in2000.rakettoppskytning.ui.bottomAppBar
import no.uio.ifi.in2000.rakettoppskytning.ui.topAppBar

fun String.isDouble(): Boolean {
    return try {
        this.toDouble()
        true
    } catch (e: NumberFormatException) {
        false
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, MapboxExperimental::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    homeScreenViewModel: HomeScreenViewModel,
    mapViewModel: MapViewModel
) {
    val scaffoldState by homeScreenViewModel.bottomSheetScaffoldState
    val favoritter = listOf<String>(
        "Lokasjon1",
        "Lokasjon2",
        "Lokasjon3",
        "Lokasjon4",
        "Lokasjon5",
        "Lokasjon6",
        "Lokasjon7",
        "Lokasjon8",
        "Lokasjon9",
        "Lokasjon10",
    )

    /*** HUSKE Å LEGGE TIL UISATE SLIK AT TING BLIR HUSKET NÅR MAN NAVIGERER!!***/

    val snackbarHostState = remember { scaffoldState.snackbarHostState }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },

        topBar = {
            topAppBar()
        },
        bottomBar = {
            bottomAppBar()
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            BottomSheetScaffold(
                scaffoldState = scaffoldState,
                sheetPeekHeight = 180.dp,       // Høyden til inputfeltet
                sheetContent = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,

                        content =
                        {
                            InputField(homeScreenViewModel = homeScreenViewModel, mapViewModel)

                            Spacer(modifier = Modifier.height(5.dp))
                            
                            LazyRow(
                                modifier = Modifier.width(340.dp),

                                content = {
                                    favoritter.forEach {
                                        item {
                                            ElevatedCard(
                                                modifier = Modifier
                                                    .height(80.dp)
                                                    .width(120.dp)
                                            ) {
                                                Text(it)

                                            }
                                            Spacer(modifier = Modifier.width(20.dp))
                                        }
                                    }
                                })
                            Spacer(modifier = Modifier.height(10.dp))

                            WeatherList(
                                homeScreenViewModel = homeScreenViewModel,
                                navController = navController
                            )
                        })
                }) {

                Map2(homeScreenViewModel, mapViewModel)

            }

        }
    }
}
