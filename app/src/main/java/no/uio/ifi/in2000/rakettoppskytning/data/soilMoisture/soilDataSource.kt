package no.uio.ifi.in2000.rakettoppskytning.data.soilMoisture

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.rakettoppskytning.model.soilMoisture.SoilMoistureHourly
import kotlin.math.roundToInt


/** Retrieves data from an open-source API with only percipation_sum as a parameter (total precipitation per day).*/
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

    Log.d("APICALL", "SoilForecast blir kallt på")

    return try {
        listOf(client.get(soilUrl).body<SoilMoistureHourly>())
    }catch (e: Exception){
        Log.d("feil i historisk-dataSource", e.toString())
        listOf()
    }

}


fun errorCheckSoilForecast(soilForecast: SoilMoistureHourly?, soilIndex: Int, hour: Int): Int? {
    if(soilForecast == null || soilIndex == -1){
        return null     // check if it exists
    }

    val i = soilIndex + hour

    if (i >= soilForecast.hourly.soil_moisture_0_to_1cm.size){
        return null     // check if it has the index
    }

    val fraction = soilForecast.hourly.soil_moisture_0_to_1cm[i]

    if (fraction == 0.0){
        return null     // check if its exactly 0.0 (if it is, the position is very likely in the sea)
    }

    return (fraction * 100).roundToInt()
}

fun getFirstSoilIndex(firstForecastDate: String?, soilForecast: SoilMoistureHourly?): Int{
    if (firstForecastDate == null || soilForecast == null){
        return -1
    }

    val formattedDate = firstForecastDate.dropLast(4)

    soilForecast.hourly.time.forEachIndexed{index, value ->
        if(formattedDate == value){
            return index
        }
    }

    return -1
}