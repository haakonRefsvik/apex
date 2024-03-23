package no.uio.ifi.in2000.rakettoppskytning.data.historicalData

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import no.uio.ifi.in2000.rakettoppskytning.model.dateNumberOfDaysAgo
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.LocationForecast
import no.uio.ifi.in2000.rakettoppskytning.model.historicalData.HistoricalData


/** Henter data fra en open-source api med kun percipation_sum som parameter (nedbør i sum per dag)*/
@RequiresApi(Build.VERSION_CODES.O)
suspend fun getHistoricalData(lat: Double, lon: Double): List<HistoricalData> {
    val client = HttpClient(CIO) {

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true

            })
        }
    }

    val url2 = "https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&daily=precipitation_sum&timezone=GMT&past_days=7&forecast_days=1"
    return try {
        listOf(client.get(url2).body<HistoricalData>())
    }catch (e: Exception){
        Log.d("feil i historisk-dataSource", e.toString())
        listOf()
    }

}
