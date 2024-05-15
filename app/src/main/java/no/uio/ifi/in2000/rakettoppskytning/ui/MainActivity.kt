package no.uio.ifi.in2000.rakettoppskytning.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.data.ApiKeyHolder
import no.uio.ifi.in2000.rakettoppskytning.network.ConnectivityManager
import no.uio.ifi.in2000.rakettoppskytning.network.NetworkSnackbar
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.RakettoppskytningTheme
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.getScreenResolution
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.screenSize
import javax.inject.Inject

@ExperimentalCoroutinesApi

@AndroidEntryPoint

class MainActivity : ComponentActivity() {
    val context = this

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApiKeyHolder.in2000ProxyKey = resources.getString(R.string.in2000ProxyKey)
        screenSize = getScreenResolution(this)
        connectivityManager = ConnectivityManager(application)

        setContent {
            RakettoppskytningTheme {
                // A surface container using the 'background' color from the theme

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Navigation(context = context)

                    val isNetworkAvailable = connectivityManager.isNetworkAvailable.value

                    // Conditional display of NetworkSnackbar only when the network is unavailable
                    if (!isNetworkAvailable) {
                        Log.d("networkSnack", "showing snackbar")
                        NetworkSnackbar()
                    }

                }
            }
        }
    }
}




