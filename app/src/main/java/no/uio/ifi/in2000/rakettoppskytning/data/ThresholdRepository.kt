package no.uio.ifi.in2000.rakettoppskytning.data

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ThresholdValues(
    /**K: parameter, V: Value*/
    var valueMap: HashMap<String, Double>   // parameterId, Value
)

class ThresholdRepository(){

    private val thresholds: ThresholdValues = exampleDataFromDataBase()
    /***/ fun updateThresholdValues(map: HashMap<String, Double>){
        thresholds.valueMap = map
        // put data back in database
    }

    /**    map["maxPrecipitation"]
     *     map["maxHumidity"]
     *     map["maxWind"]
     *     map["maxShearWind"]
     *     map["maxDewPoint"]
     * */
    fun getThresholdsMap(): HashMap<String, Double> {
        return thresholds.valueMap
    }

}

fun exampleDataFromDataBase(): ThresholdValues {
    val map = hashMapOf<String, Double>()
    map["maxPrecipitation"] = 0.0
    map["maxHumidity"] = 5.0
    map["maxWind"] = 2.0
    map["maxShearWind"] = 2.0
    map["maxDewPoint"] = -2.0

    return ThresholdValues(map)
}



