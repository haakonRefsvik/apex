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
import no.uio.ifi.in2000.rakettoppskytning.data.ApiKeyHolder
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.LocationForecast


suspend fun getForecast(lat: Double, lon: Double): List<LocationForecast> {
    if(ApiKeyHolder.in2000ProxyKey == ""){
        throw Exception("Api-key not found")
    }

    val client = HttpClient(CIO) {

        defaultRequest {
            url("https://gw-uio.intark.uh-it.no/in2000/")
            header("X-Gravitee-API-Key", ApiKeyHolder.in2000ProxyKey)
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }


    val url = "https://api.met.no/weatherapi/locationforecast/2.0/complete?lat=${lat}&lon=${lon}"
    Log.d("APICALL", "url: $url")

    return try {
        listOf(client.get(url).body<LocationForecast>())
    }catch (e: Exception){
        Log.d("mais", "dataSource")
        listOf()
    }

}
