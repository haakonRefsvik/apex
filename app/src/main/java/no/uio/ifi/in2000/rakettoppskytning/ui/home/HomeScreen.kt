package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mapbox.maps.MapboxExperimental
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteEvent
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteState
import no.uio.ifi.in2000.rakettoppskytning.ui.settings.SettingsViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.secondaryDark
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.tertiaryDark

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, MapboxExperimental::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    homeScreenViewModel: HomeScreenViewModel,
    state: FavoriteState,
    onEvent: (FavoriteEvent) -> Unit,
    mapViewModel: MapViewModel,
    settingsViewModel: SettingsViewModel
) {
    val scaffoldState by homeScreenViewModel.bottomSheetScaffoldState

    /*** HUSKE Å LEGGE TIL UISATE SLIK AT TING BLIR HUSKET NÅR MAN NAVIGERER!!***/

    val snackbarHostState = remember { scaffoldState.snackbarHostState }
    val loading = homeScreenViewModel.loading

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },

        topBar = {
            TopAppBar(navController)
        },
        bottomBar = {
            BottomAppBar(navController)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .background(color = tertiaryDark)
                .fillMaxSize()
        ) {
            BottomSheetScaffold(
                containerColor = tertiaryDark,
                contentColor = tertiaryDark, // ingen forskjell??
                scaffoldState = scaffoldState,
                sheetPeekHeight = 180.dp,       // Høyden til inputfeltet
                sheetContent = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = tertiaryDark), // ingen forskjell??
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,

                        content =
                        {
                            InputField(
                                homeScreenViewModel = homeScreenViewModel,
                                mapViewModel,
                                state,
                                onEvent
                            )


                            Spacer(modifier = Modifier.height(10.dp))

                            if (loading.value) {
                                CircularProgressIndicator()

                            } else {
                                WeatherList(
                                    homeScreenViewModel = homeScreenViewModel,
                                    navController = navController,
                                )

                            }
                        })
                }) {

                Map(homeScreenViewModel, mapViewModel)

            }

        }
    }
}
