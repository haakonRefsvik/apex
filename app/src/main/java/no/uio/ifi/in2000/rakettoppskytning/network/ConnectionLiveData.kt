package no.uio.ifi.in2000.rakettoppskytning.network

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*Inspired by:
 * https://github.com/AlexSheva-mason/Rick-Morty-Database/blob/master/app/src/main/java/com/shevaalex/android/rickmortydatabase/utils/networking/ConnectionLiveData.kt
 */
/**
 * This classes manages the different network state changes, verifies internet connectivity and also updates the application based on our current network status.

 * */


class ConnectionLiveData(context: Context) : LiveData<Boolean>() {


    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private val connection = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    private val validNetworkConnection: MutableSet<Network> = HashSet()

    private fun checkValidNetworks() {
        postValue(validNetworkConnection.size > 0)
    }

    override fun onActive() {
        networkCallback = createNetworkCallback()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NET_CAPABILITY_INTERNET)
            .build()
        connection.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun onInactive() {
        connection.unregisterNetworkCallback(networkCallback)
    }

    private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {


        override fun onAvailable(network: Network) {

            val networkCapabilities = connection.getNetworkCapabilities(network)

            val hasInternetCapability = networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)
            if (hasInternetCapability == true) {

                CoroutineScope(Dispatchers.IO).launch {
                    val hasInternet = DoesNetworkHaveInternet.execute(network.socketFactory)
                    if(hasInternet){
                        withContext(Dispatchers.Main){
                            validNetworkConnection.add(network)
                            checkValidNetworks()
                        }
                    }
                }
            }
        }


        override fun onLost(network: Network) {
            validNetworkConnection.remove(network)
            checkValidNetworks()
        }

    }

}