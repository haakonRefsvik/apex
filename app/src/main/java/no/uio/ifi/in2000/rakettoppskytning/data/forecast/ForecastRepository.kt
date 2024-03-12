package no.uio.ifi.in2000.rakettoppskytning.data.forecast

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import no.uio.ifi.in2000.rakettoppskytning.data.grib.GribRepository
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.LocationForecast
import no.uio.ifi.in2000.rakettoppskytning.model.grib.VerticalProfile
import java.io.File
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class WeatherForeCastLocationRepo() {

    private val _forecast = MutableStateFlow<List<LocationForecast>>(listOf())
    private val _verticalProfiles = MutableStateFlow<List<VerticalProfile>>(listOf())
    fun observeForecast(): StateFlow<List<LocationForecast>> = _forecast.asStateFlow()
    fun observeVerticalProfiles(): StateFlow<List<VerticalProfile>> =
        _verticalProfiles.asStateFlow()

    private val gribRepository = GribRepository()
    suspend fun loadForecast(lat: Double, lon: Double) {
        val foreCast: List<LocationForecast> = try {
            listOf(getForecast(lat, lon))


        } catch (exception: Exception) {
            listOf()
        }
        _forecast.update { foreCast }
    }

    suspend fun loadVerticalProfiles(lat: Double, lon: Double) = runBlocking {
        val gribFiles: List<File> = try {
            gribRepository.getGribFiles()
        } catch (e: Exception) {
            Log.w("VerticalProfile", "Could not load grib-files")
            listOf()
        }
        val allProfiles = createAllProfiles(gribFiles, lat, lon)

        _verticalProfiles.update { allProfiles }
    }

    suspend fun createAllProfiles(files: List<File>, lat: Double, lon: Double): List<VerticalProfile> = coroutineScope {
        files.map { file ->
            async(Dispatchers.IO) {
                VerticalProfile(lat = lat, lon = lon, file = file)
            }
        }.map { deferredProfile ->
            deferredProfile.await()
        }
    }

}