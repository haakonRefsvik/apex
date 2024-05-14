package no.uio.ifi.in2000.rakettoppskytning.ui.favorites

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.data.navigation.Routes
import no.uio.ifi.in2000.rakettoppskytning.model.formatting.formatter
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherAtPosHour
import no.uio.ifi.in2000.rakettoppskytning.ui.bars.BottomBar
import no.uio.ifi.in2000.rakettoppskytning.ui.bars.TopBar
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main50
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.secondButton0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.secondButton100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.settings0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.settings100
import java.time.ZoneOffset
import java.time.ZonedDateTime

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FavoriteCardScreen(
    navController: NavHostController,
    favoriteCardViewModel: FavoriteCardViewModel,
    homeScreenViewModel: HomeScreenViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val favorites by favoriteCardViewModel.favoriteUiState.collectAsState()
    LaunchedEffect(Unit) {
        // get, remove and refresh favorite-cards
        launch { favoriteCardViewModel.getFavoritesFromDatabase() }
        launch { favoriteCardViewModel.removeExpiredCards() }
        launch { favoriteCardViewModel.refreshWeatherData() }

    }

    Scaffold(modifier = Modifier.background(settings100),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },

        topBar = {
            TopBar(navController = navController)
        },
        bottomBar = {
            BottomBar(
                navController = navController,
                homeScreenViewModel = homeScreenViewModel,
                currentScreen = Routes.favCards
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = settings100),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Subscribed cards",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 25.sp,
                color = settings0
            )
            Spacer(modifier = Modifier.width(60.dp))
            Spacer(modifier = Modifier.height(30.dp))
            Row {
                Button(modifier = Modifier.width(155.dp),
                    colors = ButtonColors(
                        containerColor = secondButton0,
                        contentColor = secondButton100,
                        disabledContainerColor = secondButton0,
                        disabledContentColor = secondButton100
                    ),
                    onClick = {
                        favoriteCardViewModel.removeExpiredCards()
                        favoriteCardViewModel.refreshWeatherData()
                    }) {
                    if(favoriteCardViewModel.isUpdatingWeatherData.value){
                        CircularProgressIndicator(
                            modifier = Modifier.size(15.dp),
                            strokeWidth = 3.dp,
                            color = main50
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("Update cards")
                    }else{
                        Icon(
                            Icons.Default.Info,
                            modifier = Modifier.size(15.dp),
                            contentDescription = "Edit",
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("Update cards")
                    }

                }
                Spacer(modifier = Modifier.width(25.dp))
                Button(modifier = Modifier.width(155.dp),
                    colors = ButtonColors(
                        containerColor = secondButton0,
                        contentColor = secondButton100,
                        disabledContainerColor = secondButton0,
                        disabledContentColor = secondButton100
                    ),
                    onClick = {
                        favorites.favorites.forEach {
                            favoriteCardViewModel.deleteFavoriteCard(it.lat, it.lon, it.date)
                        }
                    }) {
                    Icon(
                        Icons.Default.Clear,
                        modifier = Modifier.size(15.dp),
                        contentDescription = "Edit",
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Remove all")

                }
            }
            Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Expired cards will be removed automatically",
                    color = Color.White.copy(alpha = 0.7F),
                    fontSize = 13.sp,
                )

            Spacer(modifier = Modifier.height(15.dp))

            LazyColumn(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (favorites.favorites.isEmpty()) {
                    item {

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(modifier = Modifier.height(30.dp))
                            Text(text = "No cards were found...", color = Color.White)
                        }
                    }
                }


                favorites.favorites.sortedBy {
                    ZonedDateTime.parse(it.date, formatter?.withZone(ZoneOffset.UTC))
                }
                    .forEach { fav ->
                        run {
                            item {
                                Spacer(modifier = Modifier.height(10.dp))

                                val name = remember(fav) {
                                    mutableStateOf("")
                                }
                                val weatherData = remember(favoriteCardViewModel.refreshKey) {
                                    mutableStateOf(listOf<WeatherAtPosHour>())
                                }

                                LaunchedEffect(fav) {
                                    name.value = favoriteCardViewModel.findNameByLatLon(
                                        fav.lat.toDouble(), fav.lon.toDouble()
                                    ) ?: name.value
                                }

                                LaunchedEffect(favoriteCardViewModel.refreshKey.intValue) {
                                    val dataFromApi =
                                        favoriteCardViewModel.getFavoriteWeatherData(
                                            fav.lat,
                                            fav.lon,
                                            fav.date
                                        )

                                    if (dataFromApi != null) {
                                        weatherData.value = listOf(dataFromApi)
                                    }
                                }

                                FavoriteCardElement(
                                    name = name.value,
                                    fav.lat,
                                    fav.lon,
                                    fav.date,
                                    navController,
                                    favoriteCardViewModel,
                                    weatherData = weatherData.value.firstOrNull()
                                )
                            }
                        }

                    }
            }

        }
    }
}
