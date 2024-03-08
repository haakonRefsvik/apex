package no.uio.ifi.in2000.rakettoppskytning

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import no.uio.ifi.in2000.rakettoppskytning.search.searchbarM3
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.RakettoppskytningTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RakettoppskytningTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val homeScreenViewModel: HomeScreenViewModel by viewModels()

                    val onSearch: (Double, Double) -> Unit = { latitude, longitude ->
                        homeScreenViewModel.getForecastByCord(latitude, longitude)
                    }

                    val forecastData = homeScreenViewModel.foreCastUiState.collectAsState().value

                    if (forecastData.foreCast.isEmpty()) {
                        Text(text = "")
                    } else {
                        forecastData.foreCast.forEach { forecast ->
                            Column(modifier = Modifier.fillMaxSize(),

                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center

                                ) {
                                val tider = forecast.properties.timeseries[0]
                                Text(" ${tider.data.instant.details.airTemperature}°C")
                                tider.data.next1Hours?.summary?.let { Text(it.symbolCode) }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier.fillMaxSize()
                        //contentAlignment = Alignment.Center
                    ) {
                        // Pass the desired latitude and longitude as the defaultQuery parameter
                        searchbarM3(homeScreenViewModel, onSearch)
                    }


                    //HomeScreen()
                }
            }
        }
    }
}

