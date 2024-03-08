package no.uio.ifi.in2000.rakettoppskytning.data.forecast

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.rakettoppskytning.data.api.API_KEY
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.LocationForecast

val Client = HttpClient(CIO) {

    val apiKey = API_KEY().getKey()


    defaultRequest {
        url("https://gw-uio.intark.uh-it.no/in2000/")
        header("X-Gravitee-API-Key", apiKey)
    }

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
