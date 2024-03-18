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
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.Series
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.abs

data class WeatherAtPos(
    val weatherList: List<WeatherAtPosHour> = listOf()
)

data class WeatherAtPosHour(
    val time: String,
    val lat: Double,
    val lon: Double,
    val series: Series,
    val verticalProfile: VerticalProfile?,
    val awayFromLimitPercentage: Double
)


class WeatherForeCastLocationRepo(private val thresholdRepository: ThresholdRepository) {
    private val _weatherAtPos = MutableStateFlow<WeatherAtPos>(WeatherAtPos())
    private val _forecast = MutableStateFlow<List<LocationForecast>>(listOf())
    private val _verticalProfiles = MutableStateFlow<List<VerticalProfile>>(listOf())

    fun observeWeather(): StateFlow<WeatherAtPos> = _weatherAtPos.asStateFlow()
    fun observeForecast(): StateFlow<List<LocationForecast>> = _forecast.asStateFlow()
    fun observeVerticalProfiles(): StateFlow<List<VerticalProfile>> =
        _verticalProfiles.asStateFlow()

    private val gribRepository = GribRepository()
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loadWeather(lat: Double, lon: Double, loadHours: Int = 24) {
        val gribFiles = loadGribFromDataSource(lat, lon)
        val list = mutableListOf<WeatherAtPosHour>()
        val allForecasts = loadForecastFromDataSource(lat, lon).firstOrNull()?: throw Exception("Could not fetch forecast-data")
        val allVerticalProfiles: List<VerticalProfile> = makeVerticalProfilesFromGrib(gribFiles, lat, lon)
        var hour = 0

        allForecasts.properties.timeseries.forEach{series ->
            if (hour >= loadHours){
                return@forEach
            }

            hour++
            val time = series.time
            val vp: VerticalProfile? = getVerticalProfileNearestHour(allVerticalProfiles, time)
            val awayFromLimitPercentage = getClosenessFactor(series, vp)
            Log.d("closeness", "$time, closeness: $awayFromLimitPercentage")
            val weatherAtPosHour = WeatherAtPosHour(time, lat, lon, series, vp, awayFromLimitPercentage)
            list.add(weatherAtPosHour)
        }

        val updatedWeatherAtPos = WeatherAtPos(list)
        _weatherAtPos.update { updatedWeatherAtPos }
    }

    private fun getClosenessFactor(series: Series, verticalProfile: VerticalProfile?): Double {
        val thresholds = thresholdRepository.getThresholdsMap()
        val fc = series.data.instant.details
        val fc1 = series.data.next1Hours?.details
        val closenessList = mutableListOf<Double>()
        val closenessFactor = 0.0
        // Pair (Value, Limit)
        val c1 = getCloseness(verticalProfile?.getMaxSheerWind()?.windSpeed?: 0.0, thresholds["maxShearWind"]?: 0.0)
        val c2 = getCloseness(fc.relativeHumidity, thresholds["maxHumidity"]?: 0.0)
        val c3 = getCloseness(fc.windSpeed, thresholds["maxWind"]?: 0.0)
        val c4 = getCloseness(fc1?.precipitationAmount?: Double.MAX_VALUE ,thresholds["maxPrecipitation"]?: 0.0)
        val c5 = getCloseness(fc.dewPointTemperature, thresholds["maxDewPoint"]?: 0.0, )

        closenessList.addAll(listOf(c1, c2, c3, c4, c5))

        closenessList.forEach{c ->
            if (c == 1.0){
                return 1.0  // if one value is over the limit, the function should return 1.0 to indicate status-code red
            }

            closenessFactor.plus(c)
        }

        return closenessFactor
    }

    fun getCloseness(value: Double, limit: Double, max: Boolean = true): Double{
        if(max){
            if(value >= limit){
                return 1.0
            }

            // -0.2, 1

            val d = abs(value - limit)
                    //Log.d("getCloseness", "val: $value, limit: $limit, d: ${1 - (d / limit)}")

            return abs(1 - (abs(d / limit)))
        }else{
            if(value <= limit){
                return 1.0
            }


            val d = abs(limit - value)
            //Log.d("getCloseness", "val: $value, limit: $limit, d: ${1 - (d / limit)}")
            return abs(1 - (abs(d / limit)))
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateHoursBetweenDates(vpDate: String, fcDate: String): Int {
        val dateTime1 = ZonedDateTime.parse(vpDate)
        val dateTime2 = ZonedDateTime.parse(fcDate)

        return ChronoUnit.HOURS.between(dateTime1, dateTime2).toInt()
    }

    suspend fun loadForecast(lat: Double, lon: Double) {
        val foreCast = loadForecastFromDataSource(lat, lon)
        _forecast.update { foreCast }
    }

    suspend fun loadVerticalProfiles(lat: Double, lon: Double) = coroutineScope {
        val gribFiles = loadGribFromDataSource(lat, lon)
        mutableListOf<Deferred<VerticalProfile>>()

        val allVp: List<VerticalProfile> = makeVerticalProfilesFromGrib(gribFiles, lat, lon)

        _verticalProfiles.update { allVp }
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

            val allProfiles = deferredList.awaitAll()
            Log.d("gribThread", "All threads done!")
            _verticalProfiles.update { allProfiles }

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