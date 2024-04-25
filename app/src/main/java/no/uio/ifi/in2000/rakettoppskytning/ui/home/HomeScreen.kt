package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.content.Context
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mapbox.maps.MapboxExperimental
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteEvent
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteState
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.settings.SettingsViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main100

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, MapboxExperimental::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    homeScreenViewModel: HomeScreenViewModel,
    state: FavoriteState,
    onEvent: (FavoriteEvent) -> Unit,
    mapViewModel: MapViewModel,
    settingsViewModel: SettingsViewModel,
    detailsScreenViewModel: DetailsScreenViewModel,
    context: Context
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
            BottomAppBar(navController, homeScreenViewModel)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .background(color = main100)
                .fillMaxSize()
        ) {
            BottomSheetScaffold(
                sheetContainerColor = main100,
                containerColor = main100,
                contentColor = main100,
                scaffoldState = scaffoldState,
                sheetPeekHeight = 180.dp,       // Høyden til inputfeltet
                sheetContent = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = main100),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,

                        content =
                        {
                            InputField(
                                homeScreenViewModel = homeScreenViewModel,
                                mapViewModel,
                                state,
                                onEvent,
                                context = context
                            )


                            Spacer(modifier = Modifier.height(10.dp))

                            if (loading.value) {
                                CircularProgressIndicator(color = main0)
                                Spacer(modifier = Modifier.height(30.dp))

                            } else {
                                WeatherList(
                                    homeScreenViewModel = homeScreenViewModel,
                                    navController = navController,
                                )

                            }
                        })
                }) {

                Map(detailsScreenViewModel = detailsScreenViewModel, mapViewModel)

            }
        }
    }
}
