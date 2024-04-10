package no.uio.ifi.in2000.rakettoppskytning.model.grib

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

/** Shear-wind between two isobaric layers */
class ShearWind(
    val lowerLayer: LevelData,
    val upperLayer: LevelData,
    /** Difference measured as wind-speed (m/s) */
    val windSpeed: Double,

    /** Avarage altitude between the upper and lower layer*/
    val altitude: Double = (lowerLayer.getLevelHeightInMeters() + upperLayer.getLevelHeightInMeters() ) / 2
){
    override fun toString(): String{
        return "Shear-wind at ${altitude.roundToInt()} m = ${windSpeed.roundToInt()} m/s"
    }

}

/**
 * Return a delta (difference) in wind-speed from two isobaric layers
 * The lowerLayer is layer n and upperLayer is n+1
 * */
fun getShearWind(upperLayer: LevelData, lowerLayer: LevelData): Double{
    val shearU = upperLayer.uComponentValue - lowerLayer.uComponentValue
    val shearV = upperLayer.vComponentValue - lowerLayer.vComponentValue

    val windSpeed = sqrt(shearU.pow(2) + shearV.pow(2))

    return abs(windSpeed)
}