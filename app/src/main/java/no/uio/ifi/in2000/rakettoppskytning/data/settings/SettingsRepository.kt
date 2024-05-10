package no.uio.ifi.in2000.rakettoppskytning.data.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.rakettoppskytning.data.database.RocketSpecsDao
import no.uio.ifi.in2000.rakettoppskytning.data.database.ThresholdsDao
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.Series
import no.uio.ifi.in2000.rakettoppskytning.model.grib.VerticalProfile
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.RocketSpecs
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Thresholds
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.RocketSpecType
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.RocketSpecValues
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.ThresholdType
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.ThresholdValues

class SettingsRepository(private val thresholdsDao: ThresholdsDao, private val rocketSpecsDao: RocketSpecsDao) {
    private val thresholds: ThresholdValues = runBlocking { getThresholdValues(thresholdsDao) }
    private val rocketSpecs: RocketSpecValues = runBlocking { getRocketSpecValues(rocketSpecsDao) }

    suspend fun updateThresholdValues(map: HashMap<String, Double>){
        thresholds.valueMap = map
        thresholdsDao.updateThreshold(
            mapToDatabaseObject(thresholds)
        )
    }

    suspend fun updateRocketSpecValues(map: HashMap<String, Double>){
        rocketSpecs.valueMap = map
        rocketSpecsDao.updateRocketSpecs(
            mapToDatabaseObject(rocketSpecs)
        )
    }

    fun getThresholdValue(parameter: ThresholdType): Double{
        return thresholds.valueMap[parameter.name]?: 0.0
    }

    fun getRocketSpecValue(parameter: RocketSpecType): Double{
        return rocketSpecs.valueMap[parameter.name]?: 0.0
    }

    /**
     * Returns a hashmap of how close each parameter is to the limit. If a "closeness-value" is negative, its over the limit
     * */
    fun getValueClosenessMap(series: Series, verticalProfile: VerticalProfile?, soilMoisture: Int?): HashMap<String, Double> {
        val thresholds = thresholds.valueMap
        val fc = series.data.instant.details
        val fc1 = series.data.next1Hours?.details
        val closenessMap = HashMap<String, Double>()


        val c1 = try {
            getCloseness(
                value = verticalProfile?.getMaxSheerWind()?.windSpeed?: 0.0,
                limit = thresholds[ThresholdType.MAX_SHEAR_WIND.name]?: 0.0,
            )
            }catch (e: Exception){
                0.0
            }

        val c2 = getCloseness(
            value = fc.relativeHumidity,
            limit = thresholds[ThresholdType.MAX_HUMIDITY.name]?: 0.0,
        )
        val c3 = getCloseness(
            value = fc.windSpeed,
            limit = thresholds[ThresholdType.MAX_WIND.name]?: 0.0,
        )
        val c4 = getCloseness(
            value = fc1?.precipitationAmount?:
            series.data.next6Hours?.details?.precipitationAmount?:
            series.data.next12Hours?.details?.probabilityOfPrecipitation?: 0.0
            ,
            limit = thresholds[ThresholdType.MAX_PRECIPITATION.name]?: 0.0,
        )

        val c5 = getCloseness(
            value = fc.dewPointTemperature,
            limit = thresholds[ThresholdType.MAX_DEW_POINT.name]?: 0.0,
            lowerLimit = -20.0
        )

        val c6 = getCloseness(
            value = soilMoisture?.toDouble()?: 0.0,
            limit = 100.0,
            lowerLimit = 15.0,
            max = false
        )

        closenessMap[ThresholdType.MAX_SHEAR_WIND.name] = c1
        closenessMap[ThresholdType.MAX_HUMIDITY.name] = c2
        closenessMap[ThresholdType.MAX_WIND.name] = c3
        closenessMap[ThresholdType.MAX_PRECIPITATION.name] = c4
        closenessMap[ThresholdType.MAX_DEW_POINT.name] = c5
        closenessMap["SOIL_MOISTURE"] = c6

        return closenessMap
    }

    fun getReadinessScore(map: HashMap<String, Double>): Double {
        var sum = 0.0

        map.forEach {
            if(it.value == 1.0){
                return 1.0
            }
            sum += it.value
        }

        return sum/map.size
    }

    fun getRocketSpecValuesFromRepo(): Flow<RocketSpecs?> {
        return rocketSpecsDao.getRocketSpecsById(1)
    }
    fun getThresholdValuesFromRepo(): Flow<Thresholds?> {
        return thresholdsDao.getThresholdById(1)
    }
}
/**
 * This function returns how close a value is to a limit (from 0.0 to 1.0)
 * It will return 1.0 if its over the limit
 * This is used so a card gan get its status-color
 * */

fun getCloseness(value: Double, limit: Double, lowerLimit: Double = 0.0, max: Boolean = true): Double{
    if(limit == -1.0){
        return -1.0
    }

    if(!max){
        val r = lowerLimit/value

        if(r > 1){
            return 1.0
        }

        return r
    }

    val v = value - lowerLimit
    val d = limit - lowerLimit

    val r = v/d

    if(r > 1){
        return 1.0
    }

    if(r.isNaN()){  // if we get a 0 division
        return 0.0
    }

    return r
}
