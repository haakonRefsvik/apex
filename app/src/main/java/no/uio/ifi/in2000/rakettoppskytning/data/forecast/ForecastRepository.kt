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
import kotlinx.coroutines.*
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.Series

data class WeatherAtPos(
    val weatherList: List<WeatherAtPosHour> = listOf()
)

data class WeatherAtPosHour(
    val time: String,
    val lat: Double,
    val lon: Double,
    val series: Series,
    val verticalProfile: VerticalProfile,
    val statusScore: Double
)


class WeatherForeCastLocationRepo() {
    private val _weatherAtPos = MutableStateFlow<WeatherAtPos>(WeatherAtPos())
    private val _forecast = MutableStateFlow<List<LocationForecast>>(listOf())
    private val _verticalProfiles = MutableStateFlow<List<VerticalProfile>>(listOf())

    fun observeWeather(): StateFlow<WeatherAtPos> = _weatherAtPos.asStateFlow()
    fun observeForecast(): StateFlow<List<LocationForecast>> = _forecast.asStateFlow()
    fun observeVerticalProfiles(): StateFlow<List<VerticalProfile>> =
        _verticalProfiles.asStateFlow()

    private val gribRepository = GribRepository()

    suspend fun loadWeather(lat: Double, lon: Double) {
        val foreCast = loadForecastFromDataSource(lat, lon)
        val gribFiles = loadGribFromDataSource(lat, lon)
    }
    suspend fun loadForecast(lat: Double, lon: Double) {
        val foreCast = loadForecastFromDataSource(lat, lon)
        _forecast.update { foreCast }
    }

    suspend fun loadVerticalProfiles(lat: Double, lon: Double) = coroutineScope {
        val gribFiles = loadGribFromDataSource(lat, lon)
        val deferredList = mutableListOf<Deferred<VerticalProfile>>()

        try {
            for (file in gribFiles) {
                Log.d("gribThread", "Making verticalProfile on new thread")
                val deferred = async(Dispatchers.IO) {
                    VerticalProfile(heightLimitMeters = 3000, lat = lat, lon = lon, file = file)
                }
                deferredList.add(deferred)
                Log.d("gribThread", "Thread done")
            }

            val allProfiles = deferredList.awaitAll()
            Log.d("gribThread", "All threads done!")
            _verticalProfiles.update { allProfiles }

        }catch (e: Exception){
            Log.e("GribToVerticalProfile", "Error occurred while processing vertical profiles: ${e.message}", e)
        }finally {
            for (deferred in deferredList) {
                if (!deferred.isCompleted) {
                    deferred.cancel()
                }
            }
        }
    }

    private suspend fun loadGribFromDataSource(lat: Double, lon: Double): List<File> {
        val gribFiles: List<File> = try {
            gribRepository.getGribFiles()
        } catch (e: Exception) {
            Log.w("VerticalProfile", "Could not load grib-files")
            listOf()
        }

        return gribFiles
    }
    private suspend fun loadForecastFromDataSource(lat: Double, lon: Double): List<LocationForecast> {
        val forecast: List<LocationForecast> = try {
            listOf(getForecast(lat, lon))
        } catch (exception: Exception) {
            listOf()
        }
        return forecast
    }


}