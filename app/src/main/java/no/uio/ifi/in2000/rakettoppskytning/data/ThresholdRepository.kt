package no.uio.ifi.in2000.rakettoppskytning.data

import android.util.Log
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.rakettoppskytning.data.database.ThresholdsDao
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.Series
import no.uio.ifi.in2000.rakettoppskytning.model.grib.VerticalProfile
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Thresholds

data class ThresholdValues(
    /**K: parameter, V: Value*/
    var valueMap: HashMap<String, Double>   // parameterId, Value
)


enum class ThresholdType() {
    MAX_PRECIPITATION,
    MAX_HUMIDITY,
    MAX_WIND,
    MAX_SHEAR_WIND,
    MAX_DEW_POINT,
    APOGEE
}

class ThresholdRepository(private val thresholdsDao: ThresholdsDao) {

    private val thresholds: ThresholdValues


    init {
        thresholds = runBlocking { getThresholdValues(thresholdsDao) }
    }

    suspend fun updateThresholdValues(map: HashMap<String, Double>, thresholdsDao: ThresholdsDao){
        thresholds.valueMap = map
        Log.d("kake3: ",map.toString() )
        thresholdsDao.updateThreshold(
            /*
            Thresholds(
                nedbor = thresholds.valueMap["maxPrecipitation"].toString(),
                luftfuktighet = thresholds.valueMap["maxHumidity"].toString(),
                vind = thresholds.valueMap["maxWind"].toString(),
                shearWind = thresholds.valueMap["maxShearWind"].toString(),
                duggpunkt = thresholds.valueMap["maxDewPoint"].toString(),
            )

             */
            Thresholds(
                nedbor = thresholds.valueMap[ThresholdType.MAX_PRECIPITATION.name].toString(),
                luftfuktighet = thresholds.valueMap[ThresholdType.MAX_HUMIDITY.name].toString(),
                vind = thresholds.valueMap[ThresholdType.MAX_WIND.name].toString(),
                shearWind = thresholds.valueMap[ThresholdType.MAX_SHEAR_WIND.name].toString(),
                duggpunkt = thresholds.valueMap[ThresholdType.MAX_DEW_POINT.name].toString(),
            )
        )

        // put data back in database
    }


    /**
     *     map["maxPrecipitation"]
     *     map["maxHumidity"]
     *     map["maxWind"]
     *     map["maxShearWind"]
     *     map["maxDewPoint"]
     * */
    fun getThresholdsMap(): HashMap<String, Double> {
        Log.d("kake4: ", thresholds.valueMap.toString())
        return thresholds.valueMap
    }

    /**
     * Returns a hashmap of how close each parameter is to the limit. If a "closeness-value" is negative, its over the limit
     * */

    fun getValueClosenessMap(series: Series, verticalProfile: VerticalProfile?): HashMap<String, Double> {
        val thresholds = thresholds.valueMap
        val fc = series.data.instant.details
        val fc1 = series.data.next1Hours?.details
        val closenessMap = HashMap<String, Double>()

        val c1 = getCloseness(
            value = verticalProfile?.getMaxSheerWind()?.windSpeed?: 0.0,
            limit = thresholds[ThresholdType.MAX_SHEAR_WIND.name]?: 0.0,
        )
        val c2 = getCloseness(
            value = fc.relativeHumidity,
            limit = thresholds[ThresholdType.MAX_HUMIDITY.name]?: 0.0,
        )
        val c3 = getCloseness(
            value = fc.windSpeed,
            limit = thresholds[ThresholdType.MAX_WIND.name]?: 0.0,
        )
        val c4 = getCloseness(
            value = fc1?.precipitationAmount?: Double.MAX_VALUE ,
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

        if(r.isNaN()){  // tyder på en 0.0/0.0 som ville gitt 0 som score
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

    Log.d("threshold: ", threshold.toString())

    return if (threshold != null) {
        // If threshold with ID 1 exists, create ThresholdValues from the retrieved data
        ThresholdValues(
            hashMapOf(
                /*
                "maxPrecipitation" to threshold.nedbor.toDouble(),
                "maxHumidity" to threshold.luftfuktighet.toDouble(),
                "maxWind" to threshold.vind.toDouble(),
                "maxShearWind" to threshold.shearWind.toDouble(),
                "maxDewPoint" to threshold.duggpunkt.toDouble()

                 */
                ThresholdType.MAX_PRECIPITATION.name to threshold.nedbor.toDouble(),
                ThresholdType.MAX_HUMIDITY.name to threshold.luftfuktighet.toDouble(),
                ThresholdType.MAX_WIND.name to threshold.vind.toDouble(),
                ThresholdType.MAX_SHEAR_WIND.name to threshold.shearWind.toDouble(),
                ThresholdType.MAX_DEW_POINT.name to threshold.duggpunkt.toDouble()
            )
        )
    } else {
        // If threshold with ID 1 doesn't exist, return default values
        thresholdsDao.insertThresholds(
            Thresholds(
                nedbor = "0.0",
                luftfuktighet = "90.0",
                vind = "20.0",
                shearWind = "25.0",
                duggpunkt = "5.0"
            )
        )

        return getDefaultThresholdValues()

    }
}



fun getDefaultThresholdValues(): ThresholdValues {
    /*
    val map = hashMapOf<String, Double>()
    map[ThresholdType.MAX_PRECIPITATION.name] = 0.0
    map[ThresholdType.MAX_HUMIDITY.name] = 90.0
    map[ThresholdType.MAX_WIND.name] = 20.0
    map[ThresholdType.MAX_SHEAR_WIND.name] = 25.0
    map[ThresholdType.MAX_DEW_POINT.name] = 5.0

     */

    val map = hashMapOf(
        ThresholdType.MAX_PRECIPITATION.name to 0.0,
        ThresholdType.MAX_HUMIDITY.name to 90.0,
        ThresholdType.MAX_WIND.name to 20.0,
        ThresholdType.MAX_SHEAR_WIND.name to 25.0,
        ThresholdType.MAX_DEW_POINT.name to 5.0
    )

    return ThresholdValues(map)
}





/*

data class ThresholdValues(
    /**K: parameter, V: Value*/
    var valueMap: HashMap<String, Double>   // parameterId, Value
)

enum class ThresholdType() {
    MAX_PRECIPITATION,
    MAX_HUMIDITY,
    MAX_WIND,
    MAX_SHEAR_WIND,
    MAX_DEW_POINT,
    APOGEE
}

class ThresholdRepository(){

    private val thresholds: ThresholdValues = exampleDataFromDataBase()
    fun updateThresholdValues(map: HashMap<String, Double>){
        thresholds.valueMap = map
        // put data back in database
    }

    fun getThresholdsMap(): HashMap<String, Double> {
        return thresholds.valueMap
    }

    /**
     * Returns a hashmap of how close each parameter is to the limit. If a "closeness-value" is negative, its over the limit
     * */
    fun getValueClosenessMap(series: Series, verticalProfile: VerticalProfile?): HashMap<String, Double> {
        val thresholds = thresholds.valueMap
        val fc = series.data.instant.details
        val fc1 = series.data.next1Hours?.details
        val closenessMap = HashMap<String, Double>()

        val c1 = getCloseness(
            value = verticalProfile?.getMaxSheerWind()?.windSpeed?: 0.0,
            limit = thresholds[ThresholdType.MAX_SHEAR_WIND.name]?: 0.0,
        )
        val c2 = getCloseness(
            value = fc.relativeHumidity,
            limit = thresholds[ThresholdType.MAX_HUMIDITY.name]?: 0.0,
        )
        val c3 = getCloseness(
            value = fc.windSpeed,
            limit = thresholds[ThresholdType.MAX_WIND.name]?: 0.0,
        )
        val c4 = getCloseness(
            value = fc1?.precipitationAmount?: Double.MAX_VALUE ,
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

        if(r.isNaN()){  // tyder på en 0.0/0.0 som ville gitt 0 som score
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

fun exampleDataFromDataBase(): ThresholdValues {
    val map = hashMapOf<String, Double>()
    map[ThresholdType.MAX_PRECIPITATION.name] = 0.0
    map[ThresholdType.MAX_HUMIDITY.name] = 90.0
    map[ThresholdType.MAX_WIND.name] = 20.0
    map[ThresholdType.MAX_SHEAR_WIND.name] = 25.0
    map[ThresholdType.MAX_DEW_POINT.name] = 5.0

    return ThresholdValues(map)
}
fun getDefaultThresholdValues(): ThresholdValues {
    val map = hashMapOf(
        "maxPrecipitation" to 0.0,
        "maxHumidity" to 90.0,
        "maxWind" to 20.0,
        "maxShearWind" to 25.0,
        "maxDewPoint" to 5.0
    )
    return ThresholdValues(map)
}


 */

