package no.uio.ifi.in2000.rakettoppskytning.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.core.content.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


class NetworkConnection( context: Context):LiveData<Boolean>() {

    override fun onActive() {
        super.onActive()
        checkNetworkConnectivity()
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }


    private var connectivityManager: ConnectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object: ConnectivityManager.NetworkCallback(){
        override fun onAvailable(network: Network) {
            super.onAvailable(network)

            postValue(true)
        }

        override fun onUnavailable() {
            super.onUnavailable()
            postValue(false)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            postValue(false)
        }
    }

    private fun checkNetworkConnectivity(){
        val network = connectivityManager.activeNetwork
        if(network == null)
            postValue(false)

        val requestBuilder = NetworkRequest.Builder().apply {
            addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
        }.build()

        connectivityManager.registerNetworkCallback(requestBuilder, networkCallback)
    }



}


/*


class NetworkConnection(private val context: Context):LiveData<Boolean>() {

    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private lateinit var networkConnectionCallback: ConnectivityManager.NetworkCallback
    override fun onActive() {
        super.onActive()
        updateNetworkConnection()
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                connectivityManager.registerDefaultNetworkCallback(connectionCallback())
            }
            else -> {
                context.registerReceiver(networkReciver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
            }
        }
    }
    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(connectionCallback())
    }

    private fun connectionCallback(): ConnectivityManager.NetworkCallback {
        networkConnectionCallback = object :ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                postValue(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                postValue(false)
            }
        }
        return networkConnectionCallback
    }

    private fun updateNetworkConnection() {
        Log.d("NetworkConnection", "Network status updated:")
        val networkConnection: NetworkInfo? = connectivityManager?.activeNetworkInfo
        postValue(networkConnection?.isConnected == true)
    }


    private val networkReciver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            updateNetworkConnection()
        }
    }



}

 */
