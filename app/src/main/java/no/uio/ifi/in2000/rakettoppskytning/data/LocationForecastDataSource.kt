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
import kotlinx.serialization.json.Json

suspend fun getLocationForecast(): LocationForecastComplete{
    val apiKey = API_KEY().getKey()

    val client = HttpClient(CIO){

        defaultRequest {
            url("https://gw-uio.intark.uh-it.no/in2000/")
            header("X-Gravitee-API-Key", apiKey)
        }

        install(ContentNegotiation){
            json(Json{
                ignoreUnknownKeys = true
            }
            )

        }
    }

    val pos = doubleArrayOf(60.10, 9.58) // lat , lon
    val lat: Double = pos[0]
    val lon: Double = pos[1]
    val url: String = "weatherapi/locationforecast/2.0/complete?lat=$lat&lon=$lon"

    val s: LocationForecastComplete = client.get(url).body()?: throw Exception("Could not fetch data for locationForecast")

    Log.d("API", "Dato: ${s.properties.timeseries[0].time}")
    Log.d("API", "Air temperature: ${s.properties.timeseries[0].data.instant.instantDetails.airTemperature.toString()}")

    return s
}