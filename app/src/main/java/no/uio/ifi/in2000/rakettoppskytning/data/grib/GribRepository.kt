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

class GribRepository {
    private val dataSource = GribDataSource()
    private var files = HashMap<String, File>()
    private var loaded = false

    suspend fun loadGribFiles(){
        files = dataSource.getGrib()
        Log.d("Grib", "Loading grib files from repo. DataSourceSize = ${dataSource.cachedFiles.size}")
        loaded = true
    }

    suspend fun getGribFiles(): List<File> {
        Log.d("Grib", "Getting gribfiles from repo. DataSourceSize = ${files.size}")

        while (!loaded){
            delay(100)
        }

        return files.values.toList()
    }

}