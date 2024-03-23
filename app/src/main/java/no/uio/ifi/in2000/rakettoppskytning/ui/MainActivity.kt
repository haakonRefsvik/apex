package no.uio.ifi.in2000.rakettoppskytning.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.R.string
import no.uio.ifi.in2000.rakettoppskytning.data.ApiKeyHolder
import no.uio.ifi.in2000.rakettoppskytning.data.ThresholdRepository
import no.uio.ifi.in2000.rakettoppskytning.data.database.FavoriteDatabase
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherRepository
import no.uio.ifi.in2000.rakettoppskytning.data.grib.GribRepository
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteEvent
import no.uio.ifi.in2000.rakettoppskytning.network.NetworkConnection
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.home.MapViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.settings.ThresholdViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.RakettoppskytningTheme

class MainActivity : ComponentActivity() {

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

    private lateinit var dialog: AlertDialog
    @SuppressLint("CoroutineCreationDuringComposition")
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



                    val networkManager = NetworkConnection(this)
                    networkManager.observe(this){
                        if(!it){
                            //Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show()


                            val toast = Toast(this)
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
}
