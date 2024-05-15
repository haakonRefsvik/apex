package no.uio.ifi.in2000.rakettoppskytning.data.grib

import android.util.Log
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import no.uio.ifi.in2000.rakettoppskytning.model.grib.VerticalProfile
import no.uio.ifi.in2000.rakettoppskytning.model.grib.getTime
import no.uio.ifi.in2000.rakettoppskytning.model.grib.getVerticalProfileMap
import java.io.File

/**
The GribRepository class manages the loading and retrieval of GRIB files from the GribDataSource.
 */
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

/**
 * Makes VerticalProfile-objects from a list of GRIB-files.
 * Is done asynchronously in a Thread-pool on a separate thread for each file.
 * */
suspend fun makeVerticalProfilesFromGrib(
    gribFiles: List<File>,
    lat: Double,
    lon: Double,
    maxHeight: Int
): List<VerticalProfile> = coroutineScope {
    val deferredList = mutableListOf<Deferred<VerticalProfile>>()
    try {
        for (file in gribFiles) {
            val deferred = async(Dispatchers.IO) {
                VerticalProfile(
                    heightLimitMeters = maxHeight,
                    lat = lat, lon = lon,
                    verticalProfileMap = getVerticalProfileMap(lat, lon, file, maxHeight),
                    time = getTime(file)
                )
            }
            deferredList.add(deferred)
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