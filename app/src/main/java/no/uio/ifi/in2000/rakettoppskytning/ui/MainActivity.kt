package no.uio.ifi.in2000.rakettoppskytning.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize db and thresholdRepository after context is available
        db = AppDatabase.getInstance(this)
        settingsRepository = SettingsRepository(db.thresholdsDao)

        ApiKeyHolder.in2000ProxyKey = resources.getString(R.string.in2000ProxyKey)

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
                }
            }
        }
    }
}


/*
class MainActivity : ComponentActivity() {


    private val db by lazy {
        AppDatabase.getInstance(this)
    }

    val thresholdRepository = ThresholdRepository()
    val gribRepository = GribRepository()
    val weatherRepo = WeatherRepository(thresholdRepository, gribRepository)

    val detailsScreenViewModel = DetailsScreenViewModel(weatherRepo)

    //val homeScreenViewModel = HomeScreenViewModel(weatherRepo)
    val mapViewModel = MapViewModel()
    val thresholdViewModel = ThresholdViewModel(thresholdRepository)

    private val viewModel by viewModels<HomeScreenViewModel> {
        object : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeScreenViewModel(weatherRepo, db.favoriteDao) as T
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApiKeyHolder.in2000ProxyKey = resources.getString(string.in2000ProxyKey)
        setContent {
            RakettoppskytningTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val state by viewModel.state.collectAsState()
                    Navigation(
                        state = state,
                        onEvent = viewModel::onEvent,
                        homeScreenViewModel = viewModel,
                        detailsScreenViewModel = detailsScreenViewModel,
                        weatherRepo = weatherRepo,
                        thresholdViewModel = thresholdViewModel,
                        mapViewModel = mapViewModel
                    )
                }
            }
        }
    }
}

 */
