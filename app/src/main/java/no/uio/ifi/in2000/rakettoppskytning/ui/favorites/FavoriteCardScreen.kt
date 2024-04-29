package no.uio.ifi.in2000.rakettoppskytning.ui.favorites

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.sharp.LocationOn
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.data.navigation.Routes
import no.uio.ifi.in2000.rakettoppskytning.model.formatter
import no.uio.ifi.in2000.rakettoppskytning.model.getCurrentDate
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherAtPos
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherAtPosHour
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main50
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.secondButton0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.secondButton100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.settings0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.settings100
import java.time.ZoneOffset
import java.time.ZonedDateTime

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteCardScreen(
    navController: NavHostController,
    favoriteCardViewModel: FavoriteCardViewModel,
    detailsScreenViewModel: DetailsScreenViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val favorites by favoriteCardViewModel.favoriteUiState.collectAsState()
    val favoriteWeatherData by favoriteCardViewModel.weatherDataUiState.collectAsState()

    LaunchedEffect(Unit) {
        favoriteCardViewModel.getFavoritesFromDatabase()
        favoriteCardViewModel.removeExpiredCards()
        favoriteCardViewModel.refreshWeatherData()
    }

    Scaffold(modifier = Modifier.background(settings100),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },

        topBar = {
            TopAppBar(
                colors = TopAppBarColors(settings100, settings100, settings0, settings0, settings0),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
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
        },
        bottomBar = {
            BottomAppBar(
                containerColor = main50,
                modifier = Modifier.shadow(
                    10.dp,
                    RectangleShape,
                    false,
                    DefaultShadowColor,
                    DefaultShadowColor
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = main50),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.navigate(Routes.home) }) {
                        Icon(

                            Icons.Sharp.LocationOn,
                            modifier = Modifier.size(40.dp),
                            contentDescription = "Location",
                            tint = main0
                        )
                    }
                    Spacer(modifier = Modifier.width(94.dp))
                    IconButton(onClick = { }) {
                        Icon(
                            painter = painterResource(R.drawable.rakket),
                            contentDescription = "Rakket",
                            tint = main100,

                            )
                    }
                    Spacer(modifier = Modifier.width(95.dp))
                    IconButton(
                        onClick = { navController.navigate(Routes.settings) },

                        ) {
                        Icon(
                            Icons.Sharp.Settings,
                            modifier = Modifier
                                .size(40.dp),
                            contentDescription = "Settings",
                            tint = main0,

                        )
                    }
                }
            }
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
                fontSize = 35.sp,
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
                        favoriteCardViewModel.refreshWeatherData()
                    }) {
                    Icon(
                        Icons.Default.Refresh,
                        modifier = Modifier.size(15.dp),
                        contentDescription = "Edit",
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Update cards")
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
            Spacer(modifier = Modifier.height(15.dp))

            LazyColumn(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if(favorites.favorites.isEmpty()){
                    item{

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(modifier = Modifier.height(30.dp))
                            Text(text = "No cards were found...", color = Color.White)
                            Spacer(modifier = Modifier.height(30.dp))
                            Text(
                                text = "Remember, expired cards\n will be removed automatically",
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )

                        }
                    }
                }
                if (favoriteCardViewModel.isUpdatingWeatherData.value) {
                    item {
                        CircularProgressIndicator(color = main0)
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
                else {
                    favorites.favorites.sortedBy {
                        ZonedDateTime.parse(it.date, formatter.withZone(ZoneOffset.UTC)) }
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
                                    val dataFromApi = favoriteCardViewModel.getFavoriteWeatherData(
                                        fav.lat,
                                        fav.lon,
                                        fav.date
                                    )

                                    if (dataFromApi != null) {
                                        weatherData.value = listOf(dataFromApi)
                                    }

                                    Log.d("update", "refreshing cards")
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
}
