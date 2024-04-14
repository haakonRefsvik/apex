package no.uio.ifi.in2000.rakettoppskytning.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.R.string
import no.uio.ifi.in2000.rakettoppskytning.data.ApiKeyHolder
import no.uio.ifi.in2000.rakettoppskytning.data.ThresholdRepository
import no.uio.ifi.in2000.rakettoppskytning.data.database.FavoriteDatabase
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherRepository
import no.uio.ifi.in2000.rakettoppskytning.data.grib.GribRepository
import no.uio.ifi.in2000.rakettoppskytning.network.InternetConnectionViewModel

import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.home.MapViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.settings.ThresholdViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.RakettoppskytningTheme

import no.uio.ifi.in2000.rakettoppskytning.network.ConnectivityManager

import kotlin.time.toDuration

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject


@ExperimentalCoroutinesApi

@AndroidEntryPoint

class MainActivity : ComponentActivity() {

    val internetConnectionViewModel by viewModels<InternetConnectionViewModel>()

    val thresholdRepository = ThresholdRepository()
    val gribRepository = GribRepository()
    val weatherRepo = WeatherRepository(thresholdRepository, gribRepository)

    val detailsScreenViewModel = DetailsScreenViewModel(weatherRepo)

    //val homeScreenViewModel = HomeScreenViewModel(weatherRepo)
    val mapViewModel = MapViewModel()
    val thresholdViewModel = ThresholdViewModel(thresholdRepository)


    private val db by lazy {
        FavoriteDatabase.getInstance(this)
    }

    private val viewModel by viewModels<HomeScreenViewModel> {
        object : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeScreenViewModel(weatherRepo, db.dao) as T
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


    @OptIn(ExperimentalCoroutinesApi::class)
    @SuppressLint("CoroutineCreationDuringComposition")
    @RequiresApi(Build.VERSION_CODES.O)
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
                //connectivityObserver = NetworkConnectivityObserver(applicationContext)

        ApiKeyHolder.in2000ProxyKey = resources.getString(string.in2000ProxyKey)

        // Initialize the connectivityManager
        connectivityManager = ConnectivityManager(application)

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
                        mapViewModel = mapViewModel,
                        internetConnectionViewModel = internetConnectionViewModel

                    )




                    val isNetworkAvailable = connectivityManager.isNetworkAvailable.value

                    // Use the isNetworkAvailable value to update the UI based on network availability
                    if (!isNetworkAvailable) {
                        val toast = Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG)
                        val toastLayout = layoutInflater.inflate(R.layout.toast, null)

                        toast.view = toastLayout
                        toast.duration = Toast.LENGTH_LONG

                        val toastIcon = toastLayout.findViewById<ImageView>(R.id.toast_icon)
                        toastIcon.setImageResource(R.drawable.info_24)

                        val toastText = toastLayout.findViewById<TextView>(R.id.toast_text)
                        toastText.text = "No internet connection"

                        toast.show()
                    }









                }
            }
        }
    }
}



