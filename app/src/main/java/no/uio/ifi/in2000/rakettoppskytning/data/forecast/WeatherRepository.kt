package no.uio.ifi.in2000.rakettoppskytning.data.forecast

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import no.uio.ifi.in2000.rakettoppskytning.data.grib.GribRepository
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.LocationForecast
import no.uio.ifi.in2000.rakettoppskytning.model.grib.VerticalProfile
import java.io.File
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.*
import no.uio.ifi.in2000.rakettoppskytning.data.ThresholdRepository
import no.uio.ifi.in2000.rakettoppskytning.data.historicalData.getHistoricalData
import no.uio.ifi.in2000.rakettoppskytning.model.calculateHoursBetweenDates
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.Series
import no.uio.ifi.in2000.rakettoppskytning.model.getHourFromDate
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

data class WeatherAtPos(
    val weatherList: List<WeatherAtPosHour> = listOf()
)

data class WeatherAtPosHour(
    val date: String,
    val hour: Int,
    val lat: Double,
    val lon: Double,
    val series: Series,
    val verticalProfile: VerticalProfile?,
    /**
     *     map["maxPrecipitation"]
     *     map["maxHumidity"]
     *     map["maxWind"]
     *     map["maxShearWind"]
     *     map["maxDewPoint"]
     * */
    val valuesToLimitMap: HashMap<String, Double>,
    val closeToLimitScore: Double
)

class WeatherRepository(private val thresholdRepository: ThresholdRepository, val gribRepository: GribRepository) {
    private val _weatherAtPos = MutableStateFlow(WeatherAtPos())
    fun observeWeather(): StateFlow<WeatherAtPos> = _weatherAtPos.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun thresholdValuesUpdated(){
        val weatherAtPos = _weatherAtPos.value
        val list = mutableListOf<WeatherAtPosHour>()

        weatherAtPos.weatherList.forEach {
            val closenessMap = thresholdRepository.getValueClosenessMap(it.series, it.verticalProfile)
            val score = thresholdRepository.getReadinessScore(closenessMap)

            val weatherAtPosHour = WeatherAtPosHour(it.date, getHourFromDate(it.date), it.lat, it.lon, it.series, it.verticalProfile, closenessMap, score)
            list.add(weatherAtPosHour)
        }
        val updatedWeatherAtPos = WeatherAtPos(list)
        _weatherAtPos.update { updatedWeatherAtPos }
    }

    /** Combines data from grib and forecast and makes weatherAtPos-objects from it */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loadWeather(lat: Double, lon: Double, loadHours: Int = 24) {
        try {
            val gribFiles = loadGribFromDataSource(lat, lon)
            val list = mutableListOf<WeatherAtPosHour>()
            val allForecasts: LocationForecast? = loadForecastFromDataSource(lat, lon).firstOrNull()

            val allVerticalProfiles: List<VerticalProfile> = makeVerticalProfilesFromGrib(gribFiles, lat, lon)
            var hour = 0

            allForecasts?.properties?.timeseries?.forEach{series ->
                if (hour >= loadHours){
                    return@forEach      // stops loading forecast-data when enough hours are loaded
                }

                hour++
                val date = series.time
                val vp: VerticalProfile? = getVerticalProfileNearestHour(allVerticalProfiles, date)
                val closenessMap = thresholdRepository.getValueClosenessMap(series, vp)
                val score = thresholdRepository.getReadinessScore(closenessMap)
                vp?.addGroundInfo(series)
                val weatherAtPosHour = WeatherAtPosHour(date, getHourFromDate(date), lat, lon, series, vp, closenessMap, score)
                list.add(weatherAtPosHour)
            }

            val updatedWeatherAtPos = WeatherAtPos(list)
            _weatherAtPos.update { updatedWeatherAtPos }
        }catch (e: Exception){
            _weatherAtPos.update { WeatherAtPos() }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getVerticalProfileNearestHour(allVp: List<VerticalProfile>, time: String): VerticalProfile? {
        //Log.d("detailScreenViewModel", "Tries to match time with ${allVp.size} verticalProfiles")
        val gribTimeIntervals = 3
        var output: VerticalProfile? = null
        //Log.d("timeMatch", "Prøver å matche $time")

        allVp.forEach { vp ->
            val timeDifference = calculateHoursBetweenDates(vp.time, time)

            if(timeDifference in 0..gribTimeIntervals){
                //Log.d("timeMatch", "Matchet med ${vp.time}")
                return vp
            }

        }
        //Log.d("timeMatch", "Ingen match")
        return null
    }


    private suspend fun makeVerticalProfilesFromGrib(gribFiles: List<File>, lat: Double, lon: Double): List<VerticalProfile> = coroutineScope {
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

            deferredList.awaitAll()
            Log.d("gribThread", "All threads done!")

        } catch (e: Exception) {
            Log.e(
                "GribToVerticalProfile",
                "Error occurred while processing vertical profiles: ${e.message}",
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

    @RequiresApi(Build.VERSION_CODES.O)
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
            getForecast(lat, lon)
        } catch (exception: Exception) {
            listOf()
        }
        return forecast
    }

}