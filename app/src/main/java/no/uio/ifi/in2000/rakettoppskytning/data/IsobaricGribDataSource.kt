package no.uio.ifi.in2000.rakettoppskytning.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.ph.grib2tools.grib2file.GribFile
import com.ph.grib2tools.grib2file.RandomAccessGribFile
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
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths


@Serializable
data class LatestUri(
    val uri: String
)

@RequiresApi(Build.VERSION_CODES.O)
suspend fun getGrib(){
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
    val inputStream: InputStream = client.get(latestUri.first().uri).body()?: throw Exception("Could not access the grib file")

    val file = File.createTempFile("temp", ".grib2") // Creates a temporary file
    FileOutputStream(file).use { outputStream ->
        inputStream.copyTo(outputStream)
    }

    Log.d("API", file.readBytes().size.toString())  // leser størrelse på .bin-filene (som er grib-filene) er på rundt 1,3 mill bytes som tilsvarer 1,3 mb

    val gribFile = RandomAccessGribFile("testdata", file.name) // Replace with actual typeid and source parameters

    try {
        FileInputStream(file).use { inputStream ->
            gribFile.importFromStream(inputStream, 0) // Assuming you want to import from beginning of the stream
            // The 0 parameter indicates the number of GRIB files to skip (in this case, 0 means no skip)
            // You can modify the parameters based on your requirements
        }
    } catch (e: IOException) {
        // Handle IO exception
        e.printStackTrace()
    } finally {
        file.delete() // Delete the temporary file after use (optional)
    }

    Log.d("ADSG", latestUri.first().uri)
    //val gridDefinition = gribFile.gridDefinitionTemplate as GridDefinitionTemplate30
}


