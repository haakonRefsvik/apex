package no.uio.ifi.in2000.rakettoppskytning.data

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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ucar.nc2.grib.grib2.Grib2RecordScanner
import ucar.unidata.io.RandomAccessFile
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

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

    /*
    val gribFile = StreamedGribFile("testdata", "test")
    gribFile.prepareImportFromStream(inputStream, 0);
    val s7 = gribFile.section4

    Log.d("Date: " , s7.sectionlength.toString())
     */

    parseMedNetCdf(file)
}

fun parseMedNetCdf(file: File){

    val raf: RandomAccessFile = RandomAccessFile(file.absolutePath, "r")
    val scan = Grib2RecordScanner(raf)
    while (scan.hasNext()) {
        val gr2 = scan.next()
        Log.d("Grib", gr2.id.toString())
    }
    raf.close()
}
