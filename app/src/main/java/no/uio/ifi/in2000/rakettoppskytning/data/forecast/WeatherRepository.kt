package no.uio.ifi.in2000.rakettoppskytning.data.forecast

import android.util.Log
import androidx.compose.runtime.mutableStateOf
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
import no.uio.ifi.in2000.rakettoppskytning.data.ApiKeyHolder
import no.uio.ifi.in2000.rakettoppskytning.data.settings.SettingsRepository
import no.uio.ifi.in2000.rakettoppskytning.data.soilMoisture.errorCheckSoilForecast
import no.uio.ifi.in2000.rakettoppskytning.data.soilMoisture.getFirstSoilIndex
import no.uio.ifi.in2000.rakettoppskytning.data.soilMoisture.getSoilForecast
import no.uio.ifi.in2000.rakettoppskytning.model.formatting.calculateHoursBetweenDates
import no.uio.ifi.in2000.rakettoppskytning.model.formatting.getHourFromDate
import no.uio.ifi.in2000.rakettoppskytning.model.grib.getTime
import no.uio.ifi.in2000.rakettoppskytning.model.grib.getVerticalProfileMap
import no.uio.ifi.in2000.rakettoppskytning.model.historicalData.SoilMoistureHourly
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteCard
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.RocketSpecType
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherAtPos
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherAtPosHour
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherFavorites
import kotlin.math.roundToInt


class WeatherRepository(
    private val settingsRepository: SettingsRepository,
    val gribRepository: GribRepository
) {
    private val _weatherAtPos = MutableStateFlow(WeatherAtPos())
    private val _weatherAtPosFavorite = MutableStateFlow(WeatherFavorites())
    private lateinit var _weatherAtPosOriginal: WeatherAtPos

    fun observeWeather(): StateFlow<WeatherAtPos> = _weatherAtPos.asStateFlow()
    fun observeFavorites(): StateFlow<WeatherFavorites> = _weatherAtPosFavorite.asStateFlow()

    fun toggleFavorite(lat: Double, lon: Double, date: String, value: Boolean){
        val weatherList = _weatherAtPos.value.weatherList.find {
            it.date == date && it.lat == lat && it.lon == lon
        }
        if (weatherList != null) {
            weatherList.favorite.value = value
        }

        val favoriteList = _weatherAtPosFavorite.value.weatherList.find {
            it.date == date && it.lat == lat && it.lon == lon
        }
        if (favoriteList != null) {
            favoriteList.favorite.value = value
        }

    }

    fun thresholdValuesUpdated() {
        val weatherAtPos = _weatherAtPos.value
        val updatedWeatherList = weatherAtPos.weatherList.map { weather ->
            val closenessMap =
                settingsRepository.getValueClosenessMap(weather.series, weather.verticalProfile, weather.soilMoisture)
            val score = settingsRepository.getReadinessScore(closenessMap)
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
        _weatherAtPosOriginal = updatedWeatherAtPos
        _weatherAtPos.update { updatedWeatherAtPos }
    }

    fun resetFilter() {
        _weatherAtPos.update { _weatherAtPosOriginal }

    }

    fun updateWeatherAtPos(weatherAtPos: WeatherAtPos) {
        _weatherAtPos.update { weatherAtPos }
    }

    /** Combines data from grib and forecast and makes weatherAtPos-objects from it */
    suspend fun loadWeather(lat: Double, lon: Double) {
        try {
            val list = mutableListOf<WeatherAtPosHour>()
            val allForecasts: LocationForecast? = loadForecastFromDataSource(lat, lon).firstOrNull()
            val gribFiles: List<File> = loadGribFromDataSource()
            val allVerticalProfiles: List<VerticalProfile> = makeVerticalProfilesFromGrib(gribFiles, lat, lon)
            val soilForecast: SoilMoistureHourly? = loadSoilForecast(lat, lon).firstOrNull()
            val soilIndex =
                getFirstSoilIndex(allForecasts?.properties?.timeseries?.first()?.time, soilForecast)

            Log.d("mais", "${allVerticalProfiles.size}")

            allForecasts?.properties?.timeseries?.forEachIndexed { hour, series ->

                val soilMoisture: Int? = errorCheckSoilForecast(soilForecast, soilIndex, hour)
                val date = series.time
                val vp: VerticalProfile? = getVerticalProfileNearestHour(allVerticalProfiles, date)
                val closenessMap = settingsRepository.getValueClosenessMap(series, vp, soilMoisture)
                val score = settingsRepository.getReadinessScore(closenessMap)
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
            _weatherAtPosOriginal = updatedWeatherAtPos
            _weatherAtPos.update { updatedWeatherAtPos }

        } catch (e: Exception) {
            Log.d("loadWeather", "${e.stackTrace}")
            _weatherAtPosOriginal = WeatherAtPos()
            _weatherAtPos.update { WeatherAtPos() }
        }
    }

    suspend fun loadAllFavoriteCards(favorites: List<FavoriteCard>, expiredData: Boolean){
        val list = mutableListOf<WeatherAtPosHour>()
        favorites.forEach {favCard ->
            val match = _weatherAtPosFavorite.value.weatherList.find{it.date == favCard.date && it.lon == favCard.lon.toDouble() && it.lat == favCard.lat.toDouble()}
            if(match != null && !expiredData){
                Log.d("loadFavorites", "found existing card, skipping...")
                list.add(match)
                return@forEach
            }
            val weatherAtPos = loadFavoriteCard(favCard.lat.toDouble(), favCard.lon.toDouble(), favCard.date)
            weatherAtPos.weatherList.firstOrNull()?.let { weatherHour -> list.add(weatherHour) }
        }

        val weatherData = WeatherFavorites(list)

        _weatherAtPosFavorite.update { weatherData }
    }

    suspend fun loadFavoriteCard(lat: Double, lon: Double, desiredDate: String): WeatherAtPos {
        try {
            val list = mutableListOf<WeatherAtPosHour>()
            val allForecasts: LocationForecast? = loadForecastFromDataSource(lat, lon).firstOrNull()
            val gribFiles: List<File> = loadGribFromDataSource()
            val allVerticalProfiles: List<VerticalProfile> =
                makeVerticalProfilesFromGrib(gribFiles, lat, lon)
            makeVerticalProfilesFromGrib(gribFiles, lat, lon)


            val soilForecast: SoilMoistureHourly? = loadSoilForecast(lat, lon).firstOrNull()
            val soilIndex =
                getFirstSoilIndex(allForecasts?.properties?.timeseries?.first()?.time, soilForecast)

            allForecasts?.properties?.timeseries?.forEachIndexed { hour, series ->
                if (series.time != desiredDate){
                    return@forEachIndexed
                }
                val soilMoisture: Int? = errorCheckSoilForecast(soilForecast, soilIndex, hour)
                val date = series.time
                val vp: VerticalProfile? = getVerticalProfileNearestHour(allVerticalProfiles, date)
                val closenessMap = settingsRepository.getValueClosenessMap(series, vp, soilMoisture)
                val score = settingsRepository.getReadinessScore(closenessMap)
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
                    score,
                    favorite = mutableStateOf(true)
                )
                list.add(weatherAtPosHour)
            }

            return WeatherAtPos(list)

        }catch (e: Exception) {
            return WeatherAtPos()
        }
    }

    private fun getVerticalProfileNearestHour(
        allVp: List<VerticalProfile>,
        time: String
    ): VerticalProfile? {
        val gribTimeIntervals = 3

        allVp.forEach { vp ->
            if(vp.verticalProfileMap.isEmpty()){
                Log.d("grib", "verticalProfile was empty, likely because position is out of range")
                return null
            }
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
        val heightLimit = settingsRepository.getRocketSpecValue(RocketSpecType.APOGEE).roundToInt()
        try {
            for (file in gribFiles) {
                Log.d("gribThread", "Making verticalProfile on new thread up to ${settingsRepository.getRocketSpecValue(RocketSpecType.APOGEE)} m")
                val deferred = async(Dispatchers.IO) {
                    VerticalProfile(
                        heightLimitMeters = heightLimit,
                        lat = lat, lon = lon,
                        verticalProfileMap = getVerticalProfileMap(lat, lon, file, heightLimit),
                        time = getTime(file)
                    )
                }
                deferredList.add(deferred)
                Log.d("gribThread", "Thread done")

            }

            deferredList.awaitAll()
            Log.d("gribThread", "All threads done!")

        } catch (e: Exception) {
            Log.e(
                "GribToVerticalProfile",
                "Error occurred while processing vertical profiles, restarting...",
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

    private suspend fun loadGribFromDataSource(): List<File> {
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
            getForecast(lat, lon, ApiKeyHolder.in2000ProxyKey)
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