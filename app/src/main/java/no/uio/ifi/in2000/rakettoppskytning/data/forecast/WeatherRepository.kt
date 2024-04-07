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
import no.uio.ifi.in2000.rakettoppskytning.data.soilMoisture.getSoilForecast
import no.uio.ifi.in2000.rakettoppskytning.model.calculateHoursBetweenDates
import no.uio.ifi.in2000.rakettoppskytning.model.getHourFromDate
import no.uio.ifi.in2000.rakettoppskytning.model.historicalData.Daily
import no.uio.ifi.in2000.rakettoppskytning.model.historicalData.HistoricalPrecipitation
import no.uio.ifi.in2000.rakettoppskytning.model.historicalData.SoilMoistureHourly
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherAtPos
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherAtPosHour
import kotlin.math.roundToInt

class WeatherRepository(
    private val thresholdRepository: ThresholdRepository,
    val gribRepository: GribRepository
) {
    private val _weatherAtPos = MutableStateFlow(WeatherAtPos())
    private lateinit var _weatherAtPosCpy: WeatherAtPos

    fun observeWeather(): StateFlow<WeatherAtPos> = _weatherAtPos.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun thresholdValuesUpdated() {
        val weatherAtPos = _weatherAtPos.value
        val updatedWeatherList = weatherAtPos.weatherList.map { weather ->
            val closenessMap =
                thresholdRepository.getValueClosenessMap(weather.series, weather.verticalProfile)
            val score = thresholdRepository.getReadinessScore(closenessMap)
            WeatherAtPosHour(
                weather.date,
                getHourFromDate(weather.date),
                weather.lat,
                weather.lon,
                weather.series,
                weather.verticalProfile,
                weather.soilMoisture,
                closenessMap,
                score
            )
        }
        val updatedWeatherAtPos = WeatherAtPos(updatedWeatherList)
        _weatherAtPosCpy = updatedWeatherAtPos
        _weatherAtPos.update { updatedWeatherAtPos }
    }

    fun resetFilter() {
        _weatherAtPos.update { _weatherAtPosCpy }

    }

    fun updateWeatherAtPos(weatherAtPos: WeatherAtPos) {
        _weatherAtPos.update { weatherAtPos }
    }

    /** Combines data from grib and forecast and makes weatherAtPos-objects from it */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loadWeather(lat: Double, lon: Double, loadHours: Int = 24) {
        try {
            val list = mutableListOf<WeatherAtPosHour>()

            val allForecasts: LocationForecast? = loadForecastFromDataSource(lat, lon).firstOrNull()

            val gribFiles: List<File> = loadGribFromDataSource(lat, lon)
            val allVerticalProfiles: List<VerticalProfile> =
                makeVerticalProfilesFromGrib(gribFiles, lat, lon)

            val soilForecast: SoilMoistureHourly? = loadSoilForecast(lat, lon).firstOrNull()
            val soilIndex =
                getFirstSoilIndex(allForecasts?.properties?.timeseries?.first()?.time, soilForecast)

            allForecasts?.properties?.timeseries?.forEachIndexed { hour, series ->
                if (hour >= loadHours) {
                    return@forEachIndexed
                }

                val soilMoisture: Int? = errorCheckSoilForecast(soilForecast, soilIndex, hour)
                val date = series.time
                val vp: VerticalProfile? = getVerticalProfileNearestHour(allVerticalProfiles, date)
                val closenessMap = thresholdRepository.getValueClosenessMap(series, vp)
                val score = thresholdRepository.getReadinessScore(closenessMap)
                vp?.addGroundInfo(series)
                val weatherAtPosHour = WeatherAtPosHour(
                    date,
                    getHourFromDate(date),
                    lat,
                    lon,
                    series,
                    vp,
                    soilMoisture,
                    closenessMap,
                    score
                )
                list.add(weatherAtPosHour)
            }

            val updatedWeatherAtPos = WeatherAtPos(list)
            _weatherAtPosCpy = updatedWeatherAtPos
            _weatherAtPos.update { updatedWeatherAtPos }
        } catch (e: Exception) {
            _weatherAtPos.update { WeatherAtPos() }
        }
    }

    private fun errorCheckSoilForecast(
        soilForecast: SoilMoistureHourly?,
        soilIndex: Int,
        hour: Int
    ): Int? {
        if (soilForecast == null || soilIndex == -1) {
            return null     // check if it exists
        }

        val i = soilIndex + hour

        if (i >= soilForecast.hourly.soil_moisture_0_to_1cm.size) {
            return null     // check if it has the index
        }

        val fraction = soilForecast.hourly.soil_moisture_0_to_1cm[i]

        if (fraction == 0.0) {
            return null     // check if its exactly 0.0 (if it is, the position is very likely in the sea)
        }

        return (fraction * 100).roundToInt()
    }

    private fun getFirstSoilIndex(
        firstForecastDate: String?,
        soilForecast: SoilMoistureHourly?
    ): Int {
        if (firstForecastDate == null || soilForecast == null) {
            return -1
        }

        val formattedDate = firstForecastDate.dropLast(4)

        soilForecast.hourly.time.forEachIndexed { index, value ->
            if (formattedDate == value) {
                return index
            }
        }

        return -1
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getVerticalProfileNearestHour(
        allVp: List<VerticalProfile>,
        time: String
    ): VerticalProfile? {
        val gribTimeIntervals = 3

        allVp.forEach { vp ->
            val timeDifference = calculateHoursBetweenDates(vp.time, time)

            if (timeDifference in 0..gribTimeIntervals) {
                return vp
            }

        }
        return null
    }

    private suspend fun makeVerticalProfilesFromGrib(
        gribFiles: List<File>,
        lat: Double,
        lon: Double
    ): List<VerticalProfile> = coroutineScope {
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

    private suspend fun loadForecastFromDataSource(
        lat: Double,
        lon: Double
    ): List<LocationForecast> {
        val forecast: List<LocationForecast> = try {
            getForecast(lat, lon)
        } catch (exception: Exception) {
            listOf()
        }
        return forecast
    }

    private suspend fun loadSoilForecast(lat: Double, lon: Double): List<SoilMoistureHourly> {
        val soilFc: List<SoilMoistureHourly> = try {
            getSoilForecast(lat, lon)
        } catch (exception: Exception) {
            listOf()
        }
        return soilFc
    }
}