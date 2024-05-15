package no.uio.ifi.in2000.rakettoppskytning.model.grib

import kotlin.math.atan2
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Gets height approximate height at a temperature of -60 c
 * and referencePressure of 101325.5 Pa */
fun getApproximateHeight(pressure: Double): Double{
    val standardSeaPressure = 101325.5
    val m = 0.0289644
    val r = 8.31432
    val g = 9.8066

    val c = (r * (-60 + 273.15)) / (m * g)
    val a = ln((standardSeaPressure / pressure))
    return c * a
}
/** Values for each isobaric layer, takes in a pressureLevel*/
class LevelData(val pressurePascal: Double){
    var groundLevelTempKelvin: Double = 0.0
    var tempValueKelvin: Double = 0.0
    var uComponentValue: Double = 0.0
    var vComponentValue: Double = 0.0
    var seaPressurePa: Double = 0.0
    private fun convertKelvinToCelsius(kelvin: Double): Double {
        return kelvin - 273.15
    }

    /**
     *
     * This function calculates the height in meters of a specific atmospheric level based on given temperature, pressure, and constants.
     * */
    fun getLevelHeightInMeters(): Double{
        var standardSeaPressure = 101325.5

        if(seaPressurePa > 0){
            standardSeaPressure = seaPressurePa
        }

        val m = 0.0289644
        val r = 8.31432
        val g = 9.8066

        val temp = if (groundLevelTempKelvin > 0.0){
            (tempValueKelvin + groundLevelTempKelvin) / 2
        } else {
            tempValueKelvin
        }

        val c = (r * temp) / (m * g)
        val a = ln((standardSeaPressure / pressurePascal))
        return c * a
    }

    /** Returns calculated wind-speed */
    private fun calculateWindSpeed(uComponent: Double, vComponent: Double): Double {
        return sqrt(uComponent.pow(2) + vComponent.pow(2))
    }

    /** This function calculates the wind direction in degrees and ensures the result falls within the range [0, 360) degrees.*/
    private fun calculateWindDirection(uComponent: Double, vComponent: Double): Double {
        var windDirInDegrees = Math.toDegrees(atan2(uComponent, vComponent))
        if (windDirInDegrees < 0) {
            windDirInDegrees += 360  // Ensure the direction is in the range [0, 360)
        }
        return windDirInDegrees
    }

    /** Returns celsius from kelvin*/
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

    /**
     * This function updates variables based on the provided parameter number,
     * assigning values to temperature, u-component, or v-component.
     * */
    fun addValue(parameterNumber: Int, value: Double){
        when (parameterNumber) {
            0 -> tempValueKelvin = value
            2 -> uComponentValue = value
            3 -> vComponentValue = value
        }
    }
}