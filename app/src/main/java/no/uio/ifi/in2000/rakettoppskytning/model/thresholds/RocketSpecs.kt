package no.uio.ifi.in2000.rakettoppskytning.model.thresholds

import androidx.room.PrimaryKey

data class RocketSpecValues(
    /**K: parameter (type), V: Value*/
    var valueMap: HashMap<String, Double>   // parameterId, Value
)


enum class RocketSpecType(){
    /** Highest altitude for rocket*/
    APOGEE,
    LAUNCH_ANGLE,
    LAUNCH_DIRECTION,
    THRUST_NEWTONS,
    BURN_TIME,
    DRY_WEIGHT,
    WET_WEIGHT,
    /** Resolution of rocket trajectory */
    RESOLUTION
}
