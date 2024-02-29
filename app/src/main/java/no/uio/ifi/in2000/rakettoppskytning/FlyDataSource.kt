package no.uio.ifi.in2000.rakettoppskytning

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true

        })
    }
}


suspend fun hentFly(): FlyDataClass {
    return client.get("https://api.opensky-network.org/api/states/all?lamin=58.0274&lomin=5.0328&lamax=70.66336&lomax=29.74943")
            .body()


}
