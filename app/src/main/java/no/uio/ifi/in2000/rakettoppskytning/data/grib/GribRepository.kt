package no.uio.ifi.in2000.rakettoppskytning.data.grib

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.update
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.LocationForecast
import no.uio.ifi.in2000.rakettoppskytning.model.grib.VerticalProfile
import java.io.File

class GribRepository {

    /*
    private val _gribFiles = MutableStateFlow<List<File>>(listOf())
    private var isLoading: Boolean = false
    fun observeFiles() : StateFlow<List<File>> = _gribFiles.asStateFlow()
    suspend fun loadData(){
        if (isLoading){
            return
        }
        isLoading = true

        try {
            _gribFiles.update { getGrib() }
        }catch (e: Exception){
            Log.d("Grib-API", e.toString())
            listOf<File>()
        }
        isLoading = false
    }

     */

    suspend fun loadGribFiles(){
        getGrib()
    }

    suspend fun getGribFiles(): List<File> {
        return getGrib()
    }

}