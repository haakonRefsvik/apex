package no.uio.ifi.in2000.rakettoppskytning.data

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val client = HttpClient(CIO) {

    if(ApiKeyHolder.in2000ProxyKey == ""){
        throw Exception("Fant ikke api-nøkkel")
    }

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


suspend fun hentFly() {
    val httpResponse: HttpResponse = client.get("https://api.opensky-network.org/api/states/all?lamin=58.0274&lomin=5.0328&lamax=70.66336&lomax=29.74943")


    val stringBody: String = httpResponse.body()
    Log.d("SKyJANNE",stringBody)


}
