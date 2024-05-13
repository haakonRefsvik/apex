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
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import no.uio.ifi.in2000.rakettoppskytning.R

import no.uio.ifi.in2000.rakettoppskytning.data.ApiKeyHolder
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.RakettoppskytningTheme

import no.uio.ifi.in2000.rakettoppskytning.network.ConnectivityManager

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import no.uio.ifi.in2000.rakettoppskytning.network.NetworkSnackbar
import javax.inject.Inject

@ExperimentalCoroutinesApi

@AndroidEntryPoint

class MainActivity : ComponentActivity() {
    val context = this;

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
                        NetworkSnackbar()
                    }

                }
            }
        }
    }
}




