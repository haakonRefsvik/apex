package no.uio.ifi.in2000.rakettoppskytning.model.grib

import kotlin.math.atan2
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Gets height approximate height at a temperature of 30 degrees celsius
 * (Higher temp = higher altitude)
 * and referencePressure of 101325.5 Pa */
fun getApproximateHeight(pressure: Double): Double{
    val standrardSeaPressure = 101325.5
    val m = 0.0289644
    val r = 8.31432
    val g = 9.8066

    val c = (r * (30 + 273.15)) / (m * g)
    val a = ln((standrardSeaPressure / pressure))
    return c * a
}
/** Values for each isobaric layer, takes in a pressureLevel*/
class LevelData(val pressurePascal: Double){

    var tempValueKelvin: Double = 0.0
    var uComponentValue: Double = 0.0
    var vComponentValue: Double = 0.0
    var seaPressurePa: Double = 0.0
    fun convertKelvinToCelsius(kelvin: Double): Double {
        return kelvin - 273.15
    }

    fun getLevelHeightInMeters(): Double{
        var standrardSeaPressure = 101325.5

        if(seaPressurePa > 0){
            standrardSeaPressure = seaPressurePa
        }

        val m = 0.0289644
        val r = 8.31432
        val g = 9.8066

        val c = (r * tempValueKelvin) / (m * g)
        val a = ln((standrardSeaPressure / pressurePascal))
        return c * a
    }
    fun calculateWindSpeed(uComponent: Double, vComponent: Double): Double {
        return sqrt(uComponent.pow(2) + vComponent.pow(2))
    }

    fun calculateWindDirection(uComponent: Double, vComponent: Double): Double {
        var windDirInDegrees = Math.toDegrees(atan2(uComponent, vComponent))
        if (windDirInDegrees < 0) {
            windDirInDegrees += 360  // Ensure the direction is in the range [0, 360)
        }
        return windDirInDegrees
    }

    fun getTemperatureCelsius(): Double {
        return convertKelvinToCelsius(tempValueKelvin)
    }
    /** Returns wind-speed in m/s for the isobaric layer*/
    fun getWindSpeed(): Double {
        return calculateWindSpeed(uComponentValue, vComponentValue)
    }
    /** Returns wind-direction in degrees (0 is north) for the isobaric layer*/
    fun getWindDir(): Double {
        return calculateWindDirection(uComponentValue, vComponentValue)
    }

    fun addValue(parameterNumber: Int, value: Double){
        when (parameterNumber) {
            0 -> tempValueKelvin = value
            2 -> uComponentValue = value
            3 -> vComponentValue = value
        }
    }
}