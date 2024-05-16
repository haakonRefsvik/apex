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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.rakettoppskytning.data.ApiKeyHolder
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Locale


@Serializable
data class Grib(
    val uri: String,
    val params: Params
)
@Serializable
data class Params(
    val time: String
)

class GribDataSource{
    /***
     * GRIB-data is not for a single point, but for a whole area.
     * That's why we can cache our files and reuse them when we need
     * instead of bothering the API, considering the data is not 'too' old
     * which is over 1 hour old in our case.
     */
    val cachedFiles = LinkedHashMap<String, File>()
    private var dateLastCalled: LocalDateTime? = null
    suspend fun getGrib(){
        val now = LocalDateTime.now()
        val lastCallExceedsHour = dateLastCalled?.isBefore(now.minusHours(1)) ?: true

        if (!lastCallExceedsHour && cachedFiles.isNotEmpty()){
            Log.d("APICALL", "GRIB-api was called within an hour and cache contains data. Using cache instead of calling API")
            return
        }

        val client = HttpClient(CIO){

            defaultRequest {
                url("https://gw-uio.intark.uh-it.no/in2000/")
                header("X-Gravitee-API-Key", ApiKeyHolder.in2000ProxyKey)
            }

            install(ContentNegotiation){
                json(Json{
                    ignoreUnknownKeys = true
                }
                )

            }
        }
        Log.d("APICALL", "call on the grib-API")
        val urlAvailable = "weatherapi/isobaricgrib/1.0/available.json?type=grib2"
        dateLastCalled = now

        val latestGribs: List<Grib> = try {
            client.get(urlAvailable).body()
        }catch (e: Exception){
            listOf()
        }

        Log.d("Grib", "Updating ${latestGribs.size} grib-files...")
        updateGribCache(client, latestGribs)
    }

    private suspend fun makeFile(client: HttpClient, grib: Grib, fileName: String) {
        try {
            val inputStream: InputStream = client.get(grib.uri).body()
            val file = File.createTempFile(fileName, ".grib2")

            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }

            cachedFiles[fileName] = file
        }catch (e: Exception){
            Log.d("Grib", "Could not load grib-file ${grib.uri}")
        }

    }

    /** This function makes sure that there is a upper limit of how many
     *  Grib-files can be stored in the cache.
     *  If a new grib-file is added, the oldest one will be deleted from cache
     * */
    private suspend fun updateGribCache(client: HttpClient, latestGribs: List<Grib>)= coroutineScope{
        val asyncTasks = latestGribs.map {grib ->
            val fileName = grib.params.time

            // Skips the file if it exists in the cache
            if (cachedFiles.containsKey(fileName)){
                Log.d("Grib", "'$fileName.grib2' already in cache")
                return@map null // Skip this task
            }

            // Deletes the oldest grib file if the cache is full and the new grib-file have to be added
            if (cachedFiles.size >= latestGribs.size){
                val oldestFileName = getOldestDate(cachedFiles.keys.toList())
                val oldestFile: File? = cachedFiles[oldestFileName]
                Log.d("Grib", "Trying to delete '$oldestFileName.grib2'...")
                if (oldestFile?.delete() == true){
                    Log.d("Grib", "Deleted old grib-file: '$oldestFileName.grib2'")
                    // Gets the oldest grib file and deletes it
                }
            }

            Log.d("Grib", "Added '$fileName.grib2' to cache")
            async(Dispatchers.IO) {
                makeFile(client, grib, fileName)
            }
        }
        asyncTasks.filterNotNull().map { it.await() }
    }

    /**
     * Returns the oldest date from a list of dates
     * */
    private fun getOldestDate(dates: List<String>): String{
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        var oldestDate = format.parse(dates[0])

        for (i in 1 until dates.size) {
            val currentDate = format.parse(dates[i])
            if (currentDate != null) {
                if (currentDate.before(oldestDate)) {
                    oldestDate = currentDate
                }
            }
        }

        if(oldestDate == null){ throw Exception("No oldest date found")}

        return format.format(oldestDate)
    }

}
