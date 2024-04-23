package no.uio.ifi.in2000.rakettoppskytning.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import no.uio.ifi.in2000.rakettoppskytning.R

import no.uio.ifi.in2000.rakettoppskytning.data.ApiKeyHolder
import no.uio.ifi.in2000.rakettoppskytning.data.settings.SettingsRepository
import no.uio.ifi.in2000.rakettoppskytning.data.database.AppDatabase
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherRepository
import no.uio.ifi.in2000.rakettoppskytning.data.grib.GribRepository

import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.home.MapViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.settings.SettingsViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.RakettoppskytningTheme

import no.uio.ifi.in2000.rakettoppskytning.network.ConnectivityManager

import kotlin.time.toDuration

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import javax.inject.Inject

@ExperimentalCoroutinesApi

@AndroidEntryPoint

class MainActivity : ComponentActivity() {
    val context = this;


    private lateinit var db: AppDatabase // Change to lateinit var

    private lateinit var settingsRepository: SettingsRepository // Change to lateinit var
    private val gribRepository = GribRepository()
    private val weatherRepo: WeatherRepository by lazy {
        WeatherRepository(settingsRepository, gribRepository)
    }

    private val detailsScreenViewModel by lazy {
        DetailsScreenViewModel(weatherRepo)
    }

    //val homeScreenViewModel = HomeScreenViewModel(weatherRepo)
    private val mapViewModel by lazy {
        MapViewModel()
    }

    /*
    private val thresholdViewModel by lazy {
        ThresholdViewModel(thresholdRepository)
    }

     */
    private val settingsViewModel by viewModels<SettingsViewModel> {
        object : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(settingsRepository, db.thresholdsDao) as T
            }
        }
    }

    private val viewModel by viewModels<HomeScreenViewModel> {
        object : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeScreenViewModel(weatherRepo, db.favoriteDao) as T
            }
        }
    }

    @Inject
    lateinit var connectivityManager: ConnectivityManager



    override fun onStart() {
        super.onStart()
        connectivityManager.registerConnectionObserver(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterConnectionObserver(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize db and thresholdRepository after context is available
        db = AppDatabase.getInstance(this)
        settingsRepository = SettingsRepository(db.thresholdsDao)

        ApiKeyHolder.in2000ProxyKey = resources.getString(R.string.in2000ProxyKey)

        connectivityManager = ConnectivityManager(application)

        setContent {
            RakettoppskytningTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                        val state by viewModel.state.collectAsState()
                        val thresholdState by settingsViewModel.state.collectAsState()
                        Navigation(
                            state = state,
                            onEvent = viewModel::onEvent,
                            homeScreenViewModel = viewModel,
                            detailsScreenViewModel = detailsScreenViewModel,
                            weatherRepo = weatherRepo,
                            settingsViewModel = settingsViewModel,
                            mapViewModel = mapViewModel,
                            thresholdState = thresholdState,
                            onThresholdEvent = settingsViewModel::onEvent,
                            context = context
                        )





                       val isNetworkAvailable = connectivityManager.isNetworkAvailable.value


                    // Conditional display of NetworkSnackbar only when the network is unavailable
                        if (!isNetworkAvailable) {
                            NetworkSnackbar()
                        }

                }
            }
        }
    }
}



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NetworkSnackbar() {

    val snackbarHostState = remember { SnackbarHostState() }

    Box(modifier = Modifier.fillMaxSize()) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
            snackbar = { data ->
                Snackbar(
                    snackbarData = data
                )
            }
        )

        LaunchedEffect(true) {
            snackbarHostState.showSnackbar(message = "No internet connection")
        }
    }
}


