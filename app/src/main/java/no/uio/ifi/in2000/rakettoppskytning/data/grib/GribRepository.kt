package no.uio.ifi.in2000.rakettoppskytning.data.grib

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.update
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.LocationForecast
import no.uio.ifi.in2000.rakettoppskytning.model.grib.VerticalProfile
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class GribRepository {
    private val dataSource = GribDataSource()
    suspend fun loadGribFiles(){
        dataSource.getGrib()
        Log.d("Grib", "Loading grib files from repo. Cached files in datasource = ${dataSource.cachedFiles.size}")
    }

    suspend fun getGribFiles(): List<File> {
        dataSource.getGrib()

        return dataSource.cachedFiles.values.toList()
    }

}