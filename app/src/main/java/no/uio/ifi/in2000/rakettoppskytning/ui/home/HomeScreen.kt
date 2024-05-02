package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mapbox.maps.MapboxExperimental
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteEvent
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteState
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.settings.SettingsViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.filter0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.filter50
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

                Map(
                    detailsScreenViewModel = detailsScreenViewModel,
                    mapViewModel,
                    settingsViewModel
                )
                if (mapViewModel.makeTrajectory.value) {
                    FloatingActionButton(
                        modifier = Modifier
                            .padding(start = 5.dp, top = 30.dp)
                            .heightIn(max = 35.dp),
                        contentColor = filter0,
                        containerColor = filter50,
                        onClick = {
                            mapViewModel.makeTrajectory.value = false
                        }) {
                        Row(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Remove trajectory",
                                fontSize = 12.sp,
                                modifier = Modifier.padding(5.dp)
                            )
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                modifier = Modifier.size(20.dp)
                            )
                        }


                    }
                }


            }
        }
    }
}
