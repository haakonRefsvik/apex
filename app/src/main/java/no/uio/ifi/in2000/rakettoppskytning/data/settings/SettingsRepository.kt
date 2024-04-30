package no.uio.ifi.in2000.rakettoppskytning.data.settings

import android.util.Log
import kotlinx.coroutines.flow.firstOrNull
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


class SettingsRepository(private val thresholdsDao: ThresholdsDao, rocketSpecsDao: RocketSpecsDao) {
    private val thresholds: ThresholdValues = runBlocking { getThresholdValues(thresholdsDao) }
    private val rocketSpecs: RocketSpecValues = runBlocking { getRocketSpecValues(rocketSpecsDao) }

    suspend fun updateThresholdValues(map: HashMap<String, Double>, thresholdsDao: ThresholdsDao){
        thresholds.valueMap = map
        thresholdsDao.updateThreshold(

            Thresholds(
                percipitation = getThresholdValue(ThresholdType.MAX_PRECIPITATION).toString(),
                humidity = getThresholdValue(ThresholdType.MAX_HUMIDITY).toString(),
                wind = getThresholdValue(ThresholdType.MAX_WIND).toString(),
                shearWind = getThresholdValue(ThresholdType.MAX_SHEAR_WIND).toString(),
                dewpoint = getThresholdValue(ThresholdType.MAX_DEW_POINT).toString(),
            )
        )
    }

    suspend fun updateRocketSpecValues(map: HashMap<String, Double>, rocketSpecsDao: RocketSpecsDao){
        rocketSpecs.valueMap = map

        rocketSpecsDao.updateRocketSpecs(
            RocketSpecs(
                apogee = getRocketSpecValue(RocketSpecType.APOGEE).toString(),
                launchAngle = getRocketSpecValue(RocketSpecType.LAUNCH_ANGLE).toString(),
                launchDirection = getRocketSpecValue(RocketSpecType.LAUNCH_DIRECTION).toString(),
                thrust = getRocketSpecValue(RocketSpecType.THRUST_NEWTONS).toString(),
                burntime = getRocketSpecValue(RocketSpecType.BURN_TIME).toString(),
                dryWeight = getRocketSpecValue(RocketSpecType.DRY_WEIGHT).toString(),
                wetWeight = getRocketSpecValue(RocketSpecType.WET_WEIGHT).toString(),
                dropTime = getRocketSpecValue(RocketSpecType.DROP_TIME).toString()
            )
        )
    }

    fun getThresholdsMap(): HashMap<String, Double> {
        return thresholds.valueMap
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
    fun getValueClosenessMap(series: Series, verticalProfile: VerticalProfile?): HashMap<String, Double> {
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

        closenessMap[ThresholdType.MAX_SHEAR_WIND.name] = c1
        closenessMap[ThresholdType.MAX_HUMIDITY.name] = c2
        closenessMap[ThresholdType.MAX_WIND.name] = c3
        closenessMap[ThresholdType.MAX_PRECIPITATION.name] = c4
        closenessMap[ThresholdType.MAX_DEW_POINT.name] = c5

        return closenessMap
    }

    fun getCloseness(value: Double, limit: Double, lowerLimit: Double = 0.0, max: Boolean = true): Double{
        if(limit == -1.0){
            return -1.0
        }

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

        if(r.isNaN()){  // if we get a 0 division
            return 0.0
        }

        return r
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
}



suspend fun getThresholdValues(thresholdsDao: ThresholdsDao): ThresholdValues {
    val threshold = thresholdsDao.getThresholdById(1).firstOrNull() // Retrieve the threshold with ID 1

    return if (threshold != null) {
        // If threshold with ID 1 exists, create ThresholdValues from the retrieved data
        ThresholdValues(
            hashMapOf(
                ThresholdType.MAX_PRECIPITATION.name to threshold.percipitation.toDouble(),
                ThresholdType.MAX_HUMIDITY.name to threshold.humidity.toDouble(),
                ThresholdType.MAX_WIND.name to threshold.wind.toDouble(),
                ThresholdType.MAX_SHEAR_WIND.name to threshold.shearWind.toDouble(),
                ThresholdType.MAX_DEW_POINT.name to threshold.dewpoint.toDouble()
            )
        )
    } else {
        // If threshold with ID 1 doesn't exist, return default values
        thresholdsDao.insertThresholds(
            Thresholds(
                percipitation = "0.0",
                humidity = "90.0",
                wind = "20.0",
                shearWind = "25.0",
                dewpoint = "5.0"
            )
        )

        return getDefaultThresholdValues()

    }
}

// TODO: LAGE getRocketSpecValues() funskjon tilsvarende getThresholdValues()


suspend fun getRocketSpecValues(rocketSpecsDao: RocketSpecsDao): RocketSpecValues {
    val rocketSpecs = rocketSpecsDao.getRocketSpecsById(1).firstOrNull() // Retrieve the threshold with ID 1

    return if (rocketSpecs != null) {
        // If threshold with ID 1 exists, create ThresholdValues from the retrieved data
        RocketSpecValues(
            hashMapOf(
                RocketSpecType.APOGEE.name to rocketSpecs.apogee.toDouble(),
                RocketSpecType.THRUST_NEWTONS.name to rocketSpecs.thrust.toDouble(),
                RocketSpecType.LAUNCH_ANGLE.name to rocketSpecs.launchAngle.toDouble(),
                RocketSpecType.LAUNCH_DIRECTION.name to rocketSpecs.launchDirection.toDouble(),
                RocketSpecType.BURN_TIME.name to rocketSpecs.burntime.toDouble(),
                RocketSpecType.DRY_WEIGHT.name to rocketSpecs.dryWeight.toDouble(),
                RocketSpecType.WET_WEIGHT.name to rocketSpecs.wetWeight.toDouble(),
                RocketSpecType.DROP_TIME.name to rocketSpecs.dropTime.toDouble()
            )
        )
    } else {
        // If threshold with ID 1 doesn't exist, return default values
        rocketSpecsDao.insertRocketSpecs(
            RocketSpecs(
                apogee = "3500",
                launchAngle = "80",
                launchDirection = "90",
                thrust = "4500",
                burntime = "12",
                dryWeight = "100",
                wetWeight = "130",
                dropTime = "30"
            )
        )

        return getDefaultRocketSpecs()

    }
}

fun getDefaultThresholdValues(): ThresholdValues {
    val map = hashMapOf(
        ThresholdType.MAX_PRECIPITATION.name to 0.0,
        ThresholdType.MAX_HUMIDITY.name to 90.0,
        ThresholdType.MAX_WIND.name to 20.0,
        ThresholdType.MAX_SHEAR_WIND.name to 25.0,
        ThresholdType.MAX_DEW_POINT.name to 5.0
    )

    return ThresholdValues(map)
}

fun getDefaultRocketSpecs(): RocketSpecValues {
    val map = hashMapOf(
        RocketSpecType.APOGEE.name to 3500.0,
        RocketSpecType.LAUNCH_DIRECTION.name to 90.0,
        RocketSpecType.LAUNCH_ANGLE.name to 80.0,
        RocketSpecType.THRUST_NEWTONS.name to 4500.0,
        RocketSpecType.BURN_TIME.name to 12.0,
        RocketSpecType.DRY_WEIGHT.name to 100.0,
        RocketSpecType.WET_WEIGHT.name to 130.0,
        RocketSpecType.DROP_TIME.name to 30.0,
    )

    return RocketSpecValues(map)
}

