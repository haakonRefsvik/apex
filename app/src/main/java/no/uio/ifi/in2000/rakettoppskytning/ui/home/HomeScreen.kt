package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import no.uio.ifi.in2000.rakettoppskytning.data.navigation.Routes
import no.uio.ifi.in2000.rakettoppskytning.ui.bars.BottomBar
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.details.IppcSnackbar
import no.uio.ifi.in2000.rakettoppskytning.ui.home.map.Map
import no.uio.ifi.in2000.rakettoppskytning.ui.home.map.MapViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.settings.SettingsViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.filter0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.filter50
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main100

/**
 * HomeScreen composable displays the main screen of the application.
 * It includes a bottom bar for navigation, a map view, and a bottom sheet for displaying weather details and input fields.
 * Users can interact with the map, view weather details, and switch between 2D and 3D views.
 * */
@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    homeScreenViewModel: HomeScreenViewModel,
    mapViewModel: MapViewModel,
    settingsViewModel: SettingsViewModel,
    detailsScreenViewModel: DetailsScreenViewModel,
    context: Context
) {
    val bottomSheetScaffoldState by homeScreenViewModel.bottomSheetScaffoldState
    val scaffoldState = rememberBottomSheetScaffoldState()
    val loading = homeScreenViewModel.loading
    val trajectory = remember { mapViewModel.makeTrajectory }

    Scaffold(

        snackbarHost = {
            SnackbarHost(hostState = bottomSheetScaffoldState.snackbarHostState)
        },
        bottomBar = {
            BottomBar(navController, homeScreenViewModel, Routes.home)
        },
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
                scaffoldState = bottomSheetScaffoldState,
                sheetPeekHeight = 180.dp,
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
                                    mapViewModel = mapViewModel,
                                )

                            }
                        })
                }) {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .background(color = main100)
                        .fillMaxSize()
                ) {
                    Map(
                        detailsScreenViewModel = detailsScreenViewModel,
                        mapViewModel,
                        settingsViewModel,
                        homeScreenViewModel
                    )

                    if (trajectory.value) {

                        Box {
                            Column {
                                LaunchedEffect(trajectory.value) {
                                    scaffoldState.snackbarHostState.showSnackbar("")

                                }

                                FloatingActionButton(
                                    modifier = Modifier
                                        .padding(start = 5.dp, top = 30.dp)
                                        .heightIn(max = 35.dp),
                                    contentColor = filter0,
                                    containerColor = filter50,
                                    onClick = {
                                        mapViewModel.deleteTrajectory()
                                    }) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .padding(2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Close",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }


                                }
                                FloatingActionButton(
                                    modifier = Modifier
                                        .padding(start = 5.dp, top = 5.dp)
                                        .heightIn(max = 35.dp),
                                    contentColor = filter0,
                                    containerColor = filter50,
                                    onClick = {
                                        mapViewModel.threeD.value = !mapViewModel.threeD.value
                                    }) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .padding(2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (!mapViewModel.threeD.value) {

                                            Text("3D")
                                        } else {
                                            Text("2D")
                                        }


                                    }
                                }
                                FloatingActionButton(
                                    modifier = Modifier
                                        .padding(start = 5.dp, top = 5.dp)
                                        .heightIn(max = 35.dp),
                                    contentColor = filter0,
                                    containerColor = filter50,
                                    onClick = {
                                        mapViewModel.showTraDetails.value =
                                            !mapViewModel.showTraDetails.value
                                    }) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .padding(horizontal = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        if (mapViewModel.showTraDetails.value) {
                                            Icon(
                                                Icons.Default.Info,
                                                contentDescription = "More Info",
                                                modifier = Modifier.size(22.dp),
                                                tint = Color.White
                                            )
                                        } else {
                                            Icon(
                                                Icons.Default.Info,
                                                contentDescription = "Less Info",
                                                modifier = Modifier.size(22.dp),
                                                tint = Color.White.copy(0.6F)
                                            )
                                        }
                                    }
                                }
                            }
                            IppcSnackbar(snackbarHostState = scaffoldState.snackbarHostState, context = context)
                        }
                    }
                }

            }
        }
    }
}
