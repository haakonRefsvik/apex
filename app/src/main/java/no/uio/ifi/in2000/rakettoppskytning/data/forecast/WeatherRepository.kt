package no.uio.ifi.in2000.rakettoppskytning.data.forecast

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import no.uio.ifi.in2000.rakettoppskytning.data.favoriteCards.FavoriteCardRepository
import no.uio.ifi.in2000.rakettoppskytning.data.grib.GribRepository
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.LocationForecast
import no.uio.ifi.in2000.rakettoppskytning.model.grib.VerticalProfile
import java.io.File
import no.uio.ifi.in2000.rakettoppskytning.data.grib.makeVerticalProfilesFromGrib
import no.uio.ifi.in2000.rakettoppskytning.data.settings.SettingsRepository
import no.uio.ifi.in2000.rakettoppskytning.data.soilMoisture.errorCheckSoilForecast
import no.uio.ifi.in2000.rakettoppskytning.data.soilMoisture.getFirstSoilIndex
import no.uio.ifi.in2000.rakettoppskytning.data.soilMoisture.getSoilForecast
import no.uio.ifi.in2000.rakettoppskytning.model.formatting.calculateHoursBetweenDates
import no.uio.ifi.in2000.rakettoppskytning.model.formatting.getHourFromDate
import no.uio.ifi.in2000.rakettoppskytning.model.soilMoisture.SoilMoistureHourly
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.RocketSpecType
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherAtPos
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherAtPosHour
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherFavorites
import kotlin.math.roundToInt


class WeatherRepository(
    private val settingsRepository: SettingsRepository,
    val gribRepository: GribRepository,
    private val favoriteCardRepository: FavoriteCardRepository
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
    fun updateCardColors() {
        val weatherAtPos = _weatherAtPos.value

        val updatedWeatherList = weatherAtPos.weatherList.map { weather ->
            val closenessMap = settingsRepository.getValueClosenessMap(weather.series, weather.verticalProfile, weather.soilMoisture)
            val score = settingsRepository.getReadinessScore(closenessMap)
            weather.copy(valuesToLimitMap = closenessMap, closeToLimitScore = score)
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

    suspend fun loadWeather(lat: Double, lon: Double) {
        try {
            val gribFiles: List<File> = loadGribFromDataSource()
            val updatedWeatherAtPos = makeWeatherAtPos(lat, lon, gribFiles)
            _weatherAtPosOriginal = updatedWeatherAtPos
            _weatherAtPos.update { updatedWeatherAtPos }

        } catch (e: Exception) {
            Log.d("loadWeather", "${e.stackTrace}")
            _weatherAtPosOriginal = WeatherAtPos()
            _weatherAtPos.update { WeatherAtPos() }
        }
    }

    /** Combines data from grib and forecast and makes weatherAtPos-objects from it */
    private suspend fun makeWeatherAtPos(
        lat: Double, lon: Double,
        gribFiles: List<File>,
        desiredDates: List<String> = listOf(),
        favorite: Boolean = false
    ): WeatherAtPos
    {
        val heightLimit = settingsRepository.getRocketSpecValue(RocketSpecType.APOGEE).roundToInt()
        val list = mutableListOf<WeatherAtPosHour>()
        val allForecasts: LocationForecast? = loadForecastFromDataSource(lat, lon).firstOrNull()
        val allVerticalProfiles: List<VerticalProfile> = makeVerticalProfilesFromGrib(gribFiles, lat, lon, heightLimit)
        val soilForecast: SoilMoistureHourly? = loadSoilForecast(lat, lon).firstOrNull()
        val soilIndex =
            getFirstSoilIndex(allForecasts?.properties?.timeseries?.first()?.time, soilForecast)

        allForecasts?.properties?.timeseries?.forEachIndexed { hour, series ->
            if (desiredDates.isNotEmpty() && !desiredDates.contains(series.time)){
                return@forEachIndexed
            }
            val soilMoisture: Int? = errorCheckSoilForecast(soilForecast, soilIndex, hour)
            val vp: VerticalProfile? = getVerticalProfileNearestHour(allVerticalProfiles, series.time)
            val closenessMap = settingsRepository.getValueClosenessMap(series, vp, soilMoisture)
            val score = settingsRepository.getReadinessScore(closenessMap)
            vp?.addGroundInfo(series)
            val weatherAtPosHour = WeatherAtPosHour(
                date = series.time,
                hour = getHourFromDate(series.time),
                lat,
                lon,
                series,
                verticalProfile = vp,
                soilMoisture = errorCheckSoilForecast(soilForecast, soilIndex, hour),
                closenessMap,
                score,
                favorite = mutableStateOf(favorite)
            )

            list.add(weatherAtPosHour)
        }

        return WeatherAtPos(list)
    }

    /** Takes in a list of favorite-cards, and gives them updated weather-data*/
    suspend fun loadAllFavoriteCards(expiredData: Boolean){
        val favorites = favoriteCardRepository.getAllCards()
        val gribFiles: List<File> = loadGribFromDataSource()
        val list = mutableListOf<WeatherAtPosHour>()
        // We can reduce API calls if we have cards that have the same position
        val samePositionMap = hashMapOf<Pair<Double, Double>, MutableList<String>>() // (lat, lon): Date

        favorites.forEach {
            val pos = Pair(it.lat.toDouble(), it.lon.toDouble())
            if (samePositionMap.containsKey(pos)){
                samePositionMap[pos]!!.add(it.date)
            }
            else{
                samePositionMap[pos] = mutableListOf(it.date)
            }
        }

        Log.d("loadFavorites", "made map: $samePositionMap")
        // first, make sure we don't do API-calls on cards we already have updated data on
        samePositionMap.forEach {(pos, dates) ->
            val allFavAtPos = _weatherAtPosFavorite.value.weatherList.filter { it.lat == pos.first && it.lon == pos.second }
            val posUpToDate: Boolean = allFavAtPos.map { it.date }.containsAll(dates)

            // if the position has the data for all the dates, its considered updated and can skip to the next position
            if(posUpToDate && !expiredData){
                Log.d("loadFavorites", "pos $pos is up to date, skipping...")
                list.addAll(allFavAtPos)
                return@forEach
            }

            // here we can make the API-call for each position, and not for each card
            val weatherAtPos = makeWeatherAtPos(
                pos.first, pos.second,
                gribFiles = gribFiles,
                favorite = true,
                desiredDates = dates
            )

            list.addAll(weatherAtPos.weatherList)
        }

        val weatherData = WeatherFavorites(list)

        _weatherAtPosFavorite.update { weatherData }
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