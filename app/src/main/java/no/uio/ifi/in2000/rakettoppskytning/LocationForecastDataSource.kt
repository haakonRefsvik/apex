package no.uio.ifi.in2000.rakettoppskytning

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val Client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true

        })
    }
}


suspend fun getForecast(lat: Double, lon: Double): LocationForecast {
    Log.d("APICALL", "PÅ locationForecast")
    Log.d(
        "LINK",
        "https://api.met.no/weatherapi/locationforecast/2.0/complete?lat=${lat}&lon=${lon}"
    )
    return Client.get("https://api.met.no/weatherapi/locationforecast/2.0/complete?lat=${lat}&lon=${lon}")
        .body<LocationForecast>()


}
