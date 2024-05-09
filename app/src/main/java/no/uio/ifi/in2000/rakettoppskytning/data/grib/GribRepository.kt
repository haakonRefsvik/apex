package no.uio.ifi.in2000.rakettoppskytning.data.grib

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.update
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.LocationForecast
import no.uio.ifi.in2000.rakettoppskytning.model.grib.VerticalProfile
import no.uio.ifi.in2000.rakettoppskytning.model.grib.getTime
import no.uio.ifi.in2000.rakettoppskytning.model.grib.getVerticalProfileMap
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.RocketSpecType
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class GribRepository {
    private val dataSource = GribDataSource()
    suspend fun loadGribFiles(){
        dataSource.getGrib()
        Log.d("Grib", "Done! Loaded ${dataSource.cachedFiles.size} grib-files")
    }

    suspend fun getGribFiles(): List<File> {
        dataSource.getGrib()

        return dataSource.cachedFiles.values.toList()
    }
}

suspend fun makeVerticalProfilesFromGrib(
    gribFiles: List<File>,
    lat: Double,
    lon: Double,
    maxHeight: Int
): List<VerticalProfile> = coroutineScope {
    val deferredList = mutableListOf<Deferred<VerticalProfile>>()
    try {
        for (file in gribFiles) {
            Log.d("gribThread", "Making verticalProfile on new thread up to $maxHeight m")
            val deferred = async(Dispatchers.IO) {
                VerticalProfile(
                    heightLimitMeters = maxHeight,
                    lat = lat, lon = lon,
                    verticalProfileMap = getVerticalProfileMap(lat, lon, file, maxHeight),
                    time = getTime(file)
                )
            }
            deferredList.add(deferred)
            Log.d("gribThread", "Thread done")

        }

        deferredList.awaitAll()
        Log.d("gribThread", "All threads done!")

    } catch (e: Exception) {
        Log.e(
            "GribToVerticalProfile",
            "Error occurred while processing vertical profiles, restarting...",
            e
        )
    } finally {
        for (deferred in deferredList) {
            if (!deferred.isCompleted) {
                deferred.cancel()
            }
        }
    }
    return@coroutineScope deferredList.awaitAll<VerticalProfile>()
}