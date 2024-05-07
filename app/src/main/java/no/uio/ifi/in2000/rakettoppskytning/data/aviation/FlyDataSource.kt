import com.mapbox.geojson.GeoJson
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.call.receive
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation

import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

suspend fun getRestrictedAirspace(): JsonArray? {
    val client = HttpClient(CIO) {
        install(ContentNegotiation)
    }

    try {
        val httpResponse: HttpResponse =
            client.get("https://raw.githubusercontent.com/relet/pg-xc/master/geojson/luftrom.geojson")
        val stringBody: String = httpResponse.body<String>()


        val jsonObject = Json.parseToJsonElement(stringBody).jsonObject


        val features = jsonObject["features"]?.jsonArray


        features?.forEach { feature ->
            val properties = feature.jsonObject["properties"]?.jsonObject
            val color = properties?.get("color")?.jsonPrimitive?.content
            println("Color: $color")
        }


        println("Successfully retrieved and parsed GeoJSON data.")
        return features
    } catch (e: Exception) {
        println("Error occurred: ${e.message}")
        return null
    } finally {
        client.close() // Close the HttpClient
    }
}