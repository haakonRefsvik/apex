package no.uio.ifi.in2000.rakettoppskytning.data.soilMoisture

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.rakettoppskytning.model.historicalData.SoilMoistureHourly


/** Henter data fra en open-source api med kun percipation_sum som parameter (nedbør i sum per dag)*/
suspend fun getSoilForecast(lat: Double, lon: Double): List<SoilMoistureHourly> {
    val client = HttpClient(CIO) {

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true

            })
        }
    }
    val urlPrefix = "https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}"
    val soilUrl = "$urlPrefix&hourly=soil_moisture_0_to_1cm"

    return try {
        listOf(client.get(soilUrl).body<SoilMoistureHourly>())
    }catch (e: Exception){
        Log.d("feil i historisk-dataSource", e.toString())
        listOf()
    }

}
