package no.uio.ifi.in2000.rakettoppskytning.data.grib

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.rakettoppskytning.data.api.API_KEY
import no.uio.ifi.in2000.rakettoppskytning.model.grib.VerticalProfile
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


@Serializable
data class LatestUri(
    val uri: String,
    val params: Params
)
@Serializable
data class Params(
    val time: String
)

suspend fun getGrib(): File{
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

    val urlAvailable: String = "weatherapi/isobaricgrib/1.0/available.json?type=grib2"
    val latestUri: List<LatestUri> = client.get(urlAvailable).body()?: throw Exception("Could not find the latest uri for the grib files")
    val chosenFile = latestUri.first()
    val inputStream: InputStream = client.get(chosenFile.uri).body()?: throw Exception("Could not access the grib file")

    val file = File.createTempFile("temp", ".grib2") // Creates a temporary file
    FileOutputStream(file).use { outputStream ->
        inputStream.copyTo(outputStream)
    }

    val r = VerticalProfile(59.90, 10.7, file)

    Log.d("GRIB", r.toString())
    Log.d("GRIB", r.getMaxSheerWind().toString())

    return file
}
