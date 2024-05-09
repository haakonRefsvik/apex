package no.uio.ifi.in2000.rakettoppskytning.data.airspace

import AirSpace
import AirSpaceList
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

suspend fun getAirspace(): AirSpaceList {


    val client = HttpClient(CIO) {

        install(ContentNegotiation) {
            json()

        }
    }

    try {
        val url = "https://raw.githubusercontent.com/relet/pg-xc/master/geojson/luftrom.geojson"
        Log.d("APICALL", "url: $url")
        val responseString: String = client.get(url).body()
        val json = Json { ignoreUnknownKeys = true }
        val airSpace = json.decodeFromString<AirSpace>(responseString)
        return (AirSpaceList(listOf(airSpace)))

    } catch (_: Exception) {
        return AirSpaceList(listOf())
    }

}
