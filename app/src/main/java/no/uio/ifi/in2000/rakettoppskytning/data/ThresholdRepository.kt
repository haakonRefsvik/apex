package no.uio.ifi.in2000.rakettoppskytning.data

import android.util.Log
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.rakettoppskytning.data.database.ThresholdsDao
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.Series
import no.uio.ifi.in2000.rakettoppskytning.model.grib.VerticalProfile
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Thresholds

//finne ut hvorfor den ikke opdaterer i databasen.
data class ThresholdValues(
    /**K: parameter, V: Value*/
    var valueMap: HashMap<String, Double>   // parameterId, Value
)

class ThresholdRepository(private val thresholdsDao: ThresholdsDao) {

    private val thresholds: ThresholdValues

    init {
        thresholds = runBlocking { getThresholdValues(thresholdsDao) }
    }

    suspend fun updateThresholdValues(map: HashMap<String, Double>, thresholdsDao: ThresholdsDao){
        thresholds.valueMap = map
        Log.d("threshold2: ",thresholds.toString() )
        thresholdsDao.updateThreshold(
            Thresholds(
                nedbor = thresholds.valueMap["maxPrecipitation"].toString(),
                luftfuktighet = thresholds.valueMap["maxHumidity"].toString(),
                vind = thresholds.valueMap["maxWind"].toString(),
                shearWind = thresholds.valueMap["maxShearWind"].toString(),
                duggpunkt = thresholds.valueMap["maxDewPoint"].toString(),
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
                "maxPrecipitation" to threshold.nedbor.toDouble(),
                "maxHumidity" to threshold.luftfuktighet.toDouble(),
                "maxWind" to threshold.vind.toDouble(),
                "maxShearWind" to threshold.shearWind.toDouble(),
                "maxDewPoint" to threshold.duggpunkt.toDouble()
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
    val map = hashMapOf(
        "maxPrecipitation" to 0.0,
        "maxHumidity" to 90.0,
        "maxWind" to 20.0,
        "maxShearWind" to 25.0,
        "maxDewPoint" to 5.0
    )
    return ThresholdValues(map)
}


/*

data class ThresholdValues(
    /**K: parameter, V: Value*/
    var valueMap: HashMap<String, Double>   // parameterId, Value
)

class ThresholdRepository(){

    private val thresholds: ThresholdValues = exampleDataFromDataBase()
    fun updateThresholdValues(map: HashMap<String, Double>){
        thresholds.valueMap = map
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
    map["maxPrecipitation"] = 0.0
    map["maxHumidity"] = 90.0
    map["maxWind"] = 20.0
    map["maxShearWind"] = 25.0
    map["maxDewPoint"] = 5.0

    return ThresholdValues(map)
}



 */

