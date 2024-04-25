package no.uio.ifi.in2000.rakettoppskytning.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import javax.net.SocketFactory


/**
 * Send a ping to googles primary DNS.
 * If successful, that means we have internet.
 */

object DoesNetworkHaveInternet {

    // Make sure to execute this on a background thread.
    suspend fun execute(socketFactory: SocketFactory): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("", "PINGING google.")
            val socket = socketFactory.createSocket() ?: throw IOException("Socket is null.")
            socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            socket.close()
            Log.d("", "PING success.")
            true
        } catch (e: IOException) {
            Log.e("", "No internet connection. $e")
            false
        }
    }
}