package no.uio.ifi.in2000.rakettoppskytning.model.thresholds

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
}