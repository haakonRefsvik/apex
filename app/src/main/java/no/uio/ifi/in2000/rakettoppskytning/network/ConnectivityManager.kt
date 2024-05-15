package no.uio.ifi.in2000.rakettoppskytning.network


import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LifecycleOwner
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Makes it possible for the ui components to easily observe the network status and be notified of any changes, make it easier for UI components to interact and respond to network availability.
 */
@Singleton
class ConnectivityManager
@Inject
constructor(
    application: Application,
) {
    private val connectionLiveData = ConnectionLiveData(application)

    val isNetworkAvailable = mutableStateOf(false)

    fun registerConnectionObserver(lifecycleOwner: LifecycleOwner){
        connectionLiveData.observe(lifecycleOwner) { isConnected ->
            isConnected?.let { isNetworkAvailable.value = it }
        }
    }

    fun unregisterConnectionObserver(lifecycleOwner: LifecycleOwner){
        connectionLiveData.removeObservers(lifecycleOwner)
    }
}