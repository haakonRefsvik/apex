package no.uio.ifi.in2000.rakettoppskytning.data

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class latestUri(
    val uri: String
)

@Serializable
data class available(
    val latestavailable: List<latestUri>
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

    val url: String = "weatherapi/isobaricgrib/1.0/available.json?type=grib2"
    val latestUri: available = client.get(url).body()?: throw Exception("Could not find the latest uri for the grib files")
    Log.d("API" , latestUri.latestavailable[0].uri)
}