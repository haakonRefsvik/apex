package no.uio.ifi.in2000.rakettoppskytning.data.forecast

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import no.uio.ifi.in2000.rakettoppskytning.data.grib.GribRepository
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.LocationForecast
import no.uio.ifi.in2000.rakettoppskytning.model.grib.VerticalProfile
import java.io.File


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

    suspend fun loadVerticalProfiles(lat: Double, lon: Double) {
        val gribFiles: List<File> = try {
            gribRepository.getGribFiles()
        } catch (e: Exception) {
            Log.w("VerticalProfile", "Could not load grib-files")
            listOf()
        }

        val allProfiles = mutableListOf<VerticalProfile>()
        val groundLevel = _forecast.value.firstOrNull() ?: getForecast(lat, lon)

        val timeSeriesMap = groundLevel.properties.timeseries.associateBy { it.time }

        // Adds ground-level data to the vertical profile
        gribFiles.forEach { file ->
            val verticalProfile = VerticalProfile(lat = lat, lon = lon, file = file)

            val matchingInstant = timeSeriesMap[verticalProfile.time]

            matchingInstant?.let { instant ->
                Log.d(
                    "VerticalProfile",
                    "Added ground-level data from forecast '${instant.time}' to vertical-profile '${verticalProfile.time}'"
                )
                verticalProfile.addGroundInfo(instant)
            } ?: run {
                Log.d(
                    "VerticalProfile",
                    "Could not find any forecast for '${verticalProfile.time}'"
                )
            }

            allProfiles.add(verticalProfile)
        }

        _verticalProfiles.update { allProfiles }
        Log.d("Verticalprofile", "Shearwind: ${allProfiles.first().getMaxSheerWind()}")
    }

}