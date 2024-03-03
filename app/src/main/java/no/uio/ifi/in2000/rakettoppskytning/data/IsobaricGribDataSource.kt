package no.uio.ifi.in2000.rakettoppskytning.data

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.InputStream


@Serializable
data class LatestUri(
    val uri: String
)

suspend fun getGrib(){
    val client = HttpClient(CIO){

        defaultRequest {
            url("https://gw-uio.intark.uh-it.no/in2000/")
            header("X-Gravitee-API-Key", "df0249c5-183e-442e-833b-7d255d26521d")
        }

        install(ContentNegotiation){
            json(Json{
                ignoreUnknownKeys = true
            }
            )

        }
    }

    val urlAvailable: String = "weatherapi/isobaricgrib/1.0/available.json?type=grib2"
    val latestUri: List<LatestUri> = client.get(urlAvailable).body()?: throw Exception("Could not find the latest uri for the grib files")
    val inputStream: InputStream = client.get(latestUri.first().uri).body()?: throw Exception("Could not access the grib file")

    val file = File.createTempFile("temp", ".bin") // Creates a temporary file
    FileOutputStream(file).use { outputStream ->
        inputStream.copyTo(outputStream)
    }

    Log.d("API", file.readBytes().size.toString())  // leser størrelse på .bin-filene (som er grib-filene) er på rundt 1,3 mill bytes som tilsvarer 1,3 mb

}
