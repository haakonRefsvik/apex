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
    /** Hashmap of difference between a parameter value, and a parameter limit */
    val valuesToLimitMap: HashMap<String, Double>,
    val closeToLimitScore: Double
)

class WeatherAtPosRepo(private val thresholdRepository: ThresholdRepository, val gribRepository: GribRepository) {
    private val _weatherAtPos = MutableStateFlow(WeatherAtPos())
    private val _forecast = MutableStateFlow<List<LocationForecast>>(listOf())
    private val _verticalProfiles = MutableStateFlow<List<VerticalProfile>>(listOf())


    fun observeWeather(): StateFlow<WeatherAtPos> = _weatherAtPos.asStateFlow()
    fun observeForecast(): StateFlow<List<LocationForecast>> = _forecast.asStateFlow()
    fun observeVerticalProfiles(): StateFlow<List<VerticalProfile>> =
        _verticalProfiles.asStateFlow()

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
            val date = series.time
            val vp: VerticalProfile? = getVerticalProfileNearestHour(allVerticalProfiles, date)
            val closenessMap = getValueClosenessMap(series, vp)
            val score = getReadinessScore(closenessMap)
            vp?.addGroundInfo(series)
            //Log.d("closeness", "$time, closeness: $closenessMap")
            //Log.d("closeness", "Score: $score")

            val weatherAtPosHour = WeatherAtPosHour(date, getHourFromDate(date), lat, lon, series, vp, closenessMap, score)
            list.add(weatherAtPosHour)
        }

        val updatedWeatherAtPos = WeatherAtPos(list)
        _weatherAtPos.update { updatedWeatherAtPos }
    }



    private fun getReadinessScore(map: HashMap<String, Double>): Double {
        var sum = 0.0

        map.forEach {
            if(it.value == 1.0){
                return 1.0
            }

            sum += it.value
        }

        return sum/map.size
    }

    /**
     * Returns a hashmap of how close each parameter is to the limit. If a "closeness-value" is negative, its over the limit
     * */

    private fun getValueClosenessMap(series: Series, verticalProfile: VerticalProfile?): HashMap<String, Double> {
        val thresholds = thresholdRepository.getThresholdsMap()
        val fc = series.data.instant.details
        val fc1 = series.data.next1Hours?.details
        val closenessMap = HashMap<String, Double>()

        val c1 = getCloseness(
            value = verticalProfile?.getMaxSheerWind()?.windSpeed?: 0.0,
            limit = thresholds["maxShearWind"]?: 0.0,
        )
        val c2 = getCloseness(
            value = fc.relativeHumidity,
            limit = thresholds["maxHumidity"]?: 0.0,
        )
        val c3 = getCloseness(
            value = fc.windSpeed,
            limit = thresholds["maxWind"]?: 0.0,
        )
        val c4 = getCloseness(
            value = fc1?.precipitationAmount?: Double.MAX_VALUE ,
            limit = thresholds["maxPrecipitation"]?: 0.0,
        )

        val c5 = getCloseness(
            value = fc.dewPointTemperature,
            limit = thresholds["maxDewPoint"]?: 0.0,
            lowerLimit = -20.0
        )

        closenessMap["maxShearWind"] = c1
        closenessMap["maxHumidity"] = c2
        closenessMap["maxWind"] = c3
        closenessMap["maxPrecipitation"] = c4
        closenessMap["maxDewPoint"] = c5

        return closenessMap
    }

    fun getCloseness(value: Double, limit: Double, lowerLimit: Double = 0.0, max: Boolean = true): Double{
        if(!max){
            //TODO() NOT IMPLEMENTED
            return 1.0
        }

        val v = value - lowerLimit
        val d = limit - lowerLimit

        val r = v/d

        if(r > 1){
            return 1.0
        }

        if(r.isNaN()){  // tyder på en 0.0/0.0 som ville gitt 0 som score
            return 0.0
        }

        return r
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
        val dateTime1 = ZonedDateTime.parse(vpDate)?: return -1
        val dateTime2 = ZonedDateTime.parse(fcDate)?: return -1

        return ChronoUnit.HOURS.between(dateTime1, dateTime2).toInt()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getHourFromDate(date: String): Int {
        return ZonedDateTime.parse(date).hour
    }

    /*
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

     */

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