package no.uio.ifi.in2000.rakettoppskytning.data.ballistic

import android.util.Log
import no.uio.ifi.in2000.rakettoppskytning.model.grib.LevelData
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

class Point(
    val x: Double,
    val y: Double,
    val z: Double,
    val timeS: Double,
    val parachuted: Boolean = false
) {
    override fun toString(): String {
        return "(x: ${x.roundToInt()}, y: ${y.roundToInt()}, z: ${z.roundToInt()}, time: ${timeS.toInt()}, parachuted: $parachuted)"
    }
}

/**Considered alt(itude) is between lower and upperlayer, calculate the ratio between the two.
 * The returning pair consists of two doubles that add up to 1 like (lower, upper) */
fun getLinearRatios(lowerAlt: Double, upperAlt: Double, alt: Double): Pair<Double, Double>? {
    val altBetweenLayers = upperAlt - lowerAlt

    if (alt > upperAlt || alt < lowerAlt) {
        return null
    }

    val d = alt - lowerAlt
    val p1 = d / altBetweenLayers

    return Pair(1 - p1, p1)
}


fun getSigmoidRatios(
    lowerAlt: Double,
    upperAlt: Double,
    alt: Double,
    steepness: Double = 0.01
): Pair<Double, Double>? {
    if (alt > upperAlt || alt < lowerAlt) {
        return null
    }

    // If altitude is the same as lower layer, return (1.0, 0.0)
    if (alt == lowerAlt) return Pair(1.0, 0.0)

    // If altitude is the same as upper layer, return (0.0, 1.0)
    if (alt == upperAlt) return Pair(0.0, 1.0)

    // Calculate the midpoint between the layers
    val midpoint = (lowerAlt + upperAlt) / 2.0

    // Calculate the distance from the midpoint
    val distanceFromMidpoint = abs(alt - midpoint)

    // Calculate the sigmoid function value using a parameter for the curve steepness
    val sigmoidValue = 1 / (1 + exp(-steepness * distanceFromMidpoint))

    // Scale the sigmoid value to ensure it ranges from 0.0 to 1.0
    val scaledSigmoidValue = sigmoidValue / (sigmoidValue + (1 - sigmoidValue))

    // Determine which side of the midpoint the altitude lies on
    val lowerRatio = if (alt < midpoint) scaledSigmoidValue else 1 - scaledSigmoidValue
    val upperRatio = if (alt < midpoint) 1 - scaledSigmoidValue else scaledSigmoidValue

    return Pair(lowerRatio, upperRatio)
}

fun mergeLevelData(ratios: Pair<Double, Double>, lowerData: Double, upperData: Double): Double {
    return (ratios.first * lowerData) + (ratios.second * upperData)
}

fun findLowerUpperLevel(allLevels: List<LevelData>, altitude: Double): Pair<LevelData, LevelData>? {
    val sortedLevels = allLevels.sortedByDescending { it.pressurePascal }

    sortedLevels.forEachIndexed { index, _ ->
        val l = sortedLevels[index]
        if (index == sortedLevels.lastIndex) {
            return Pair(l, l)
        }
        val u = sortedLevels[index + 1]

        if (l.getLevelHeightInMeters() <= altitude && altitude <= u.getLevelHeightInMeters()) {
            return Pair(l, u)
        }
    }

    return null
}

fun getNearestLevelData(allLevels: List<LevelData>, altitudeMeters: Double): LevelData{
    var nearest: LevelData = allLevels.maxBy { it.pressurePascal }  // Gets the highest level as nearest

    var nearestAlt = abs(nearest.getLevelHeightInMeters() - altitudeMeters)

    allLevels.forEach {
        val levelAlt = it.getLevelHeightInMeters()
        val d = abs(levelAlt - altitudeMeters)

        if (d < nearestAlt) {
            nearest = it
            nearestAlt = d
        }
    }

    return nearest
}

/**
 * Function to deal with numbers really close to zero
 * */
fun isCloseToZero(number: Double, threshold: Double = 1e-10): Boolean {
    return abs(number) < threshold
}

fun calculateAirDensity(pressure: Double, temperature: Double): Double {
    val gasConstant = 287.058 // Specific gas constant for dry air in J/kg·K
    val kelvinTemperature = temperature + 273.15 // Convert temperature from Celsius to Kelvin
    return (pressure) / (gasConstant * kelvinTemperature)
}

/**
 * This function takes in the two isobaric layers you are between (in altitude) and
 * gives you an estimated air-density based on the air density of those two layers -
 * based on how far you are from one layer to the next
 * */
fun getAirDensityLinear(low: LevelData, upp: LevelData, alt: Double, rho: Double): Double {
    val altL = low.getLevelHeightInMeters()
    val altU = upp.getLevelHeightInMeters()
    
    return try {
        calculateAirDensity(
            pressure = mergeLevelData(
                getLinearRatios(altL, altU, alt)!!,
                low.pressurePascal,
                upp.pressurePascal),
            temperature = mergeLevelData(
                getLinearRatios(altL, altU, alt)!!,
                low.getTemperatureCelsius(),
                upp.getTemperatureCelsius()))
    }catch (e: Exception){
        rho
    }

}
/**
 * This function takes in the two isobaric layers you are between (in altitude) and
 * gives you the estimated wind (u or v-component) of those two layers -
 * based on how far you are from one layer to the next non-linear
 * */
fun getWindSigmoid(low: LevelData, upp: LevelData, alt: Double, windLow: Double, windHigh: Double): Double {
    val altL = low.getLevelHeightInMeters()
    val altU = upp.getLevelHeightInMeters()
    val ratios = getSigmoidRatios(altL, altU, alt) ?: return 0.0

    return mergeLevelData(
        ratios,
        windLow,
        windHigh
    )
}

fun simulateTrajectory(
    burnTime: Double,
    launchAngle: Double,
    launchDir: Double,
    altitude: Double,
    thrust: Double,
    apogee: Double,
    mass: Double,
    massDry: Double,
    dt: Double,
    allLevels: List<LevelData>,
    vAfterParachute: Double = 8.6
): List<Point>{

    val g = 9.81
    var rho = 1.225
    val cd = 0.5
    val launchAngleRad = Math.toRadians(launchAngle)
    val launchDirRad = Math.toRadians(launchDir)

    var x = 0.0
    var y = 0.0
    var z: Double = altitude

    var vx = 0.0
    var vy = 0.0
    var vz = 0.0

    var secondsUsed = 0.0
    var parachuteDeployed = false

    val massDifference = mass - massDry
    val massLossPerSecond = (massDifference / burnTime)
    var currentMass = mass

    var burnTimeLeft = burnTime
    var ax: Double
    var ay: Double
    var az: Double

    val list = mutableListOf<Point>()

    var xWind = 0.0
    var yWind = 0.0
    var currLowerUpper: Pair<LevelData, LevelData>?
    var lastZ = -100.0

    while (z >= altitude) {
        val p = Point(x, y, z, secondsUsed, false)
        list.add(p)

        if (abs(lastZ - z) > 100) {
            // Update ratios each 100 meters altitude
            currLowerUpper = findLowerUpperLevel(allLevels, z)
            lastZ = z

            if (currLowerUpper != null) {
                val lowLevel = currLowerUpper.first
                val uppLevel = currLowerUpper.second
                rho = getAirDensityLinear(lowLevel, uppLevel, z, rho)
                xWind = getWindSigmoid(
                    lowLevel,
                    uppLevel,
                    z,
                    lowLevel.vComponentValue,
                    uppLevel.vComponentValue
                )
                yWind = getWindSigmoid(
                    lowLevel,
                    uppLevel,
                    z,
                    lowLevel.uComponentValue,
                    uppLevel.uComponentValue
                )
            }
        }

        if (burnTimeLeft >= 0 && z <= apogee) {
            ax = thrust * cos(launchAngleRad) * sin(launchDirRad) / currentMass
            if (isCloseToZero(ax)) {
                ax = 0.0
            }
            ay = thrust * cos(launchAngleRad) * cos(launchDirRad) / currentMass
            if (isCloseToZero(ay)) {
                ay = 0.0
            }
            az = thrust * sin(launchAngleRad) / currentMass - g
            burnTimeLeft -= dt

            if (burnTime > 0) {
                currentMass -= (massLossPerSecond * dt) // decrease mass
            }

        } else {
            ax = (xWind / currentMass)
            ay = (yWind / currentMass)
            az = -g

            val v = sqrt(vx.pow(2.0) + vy.pow(2.0) + vz.pow(2.0))
            val fDragX = -0.5 * rho * cd * v * vx
            val fDragY = -0.5 * rho * cd * v * vy
            val fDragZ = -0.5 * rho * cd * v * vz

            ax += fDragX / currentMass
            ay += fDragY / currentMass
            az += fDragZ / currentMass

        }

        vx += (ax * dt)
        vy += ay * dt
        vz += az * dt

        x += vx * dt
        y += vy * dt
        z += vz * dt

        secondsUsed += dt

        if (vz < 0 && !parachuteDeployed) {
            parachuteDeployed = true
            if(allLevels.isNotEmpty()){
                val parachuteTrajectory =
                    simulateParachute(x, y, z, 1.0, secondsUsed, allLevels, vAfterParachute)
                list.addAll(parachuteTrajectory)
            }

        }
    }

    return list
}


fun simulateParachute(
    xInit: Double,
    yInit: Double,
    zInit: Double,
    timeStep: Double,
    s: Double,
    allLevels: List<LevelData>,
    parachuteVelocityDown: Double
): List<Point> {
    var x = xInit
    var y = yInit
    var z = zInit

    var vx: Double
    var vy: Double
    var vz: Double

    var xWind = 0.0
    var yWind = 0.0
    var currLowerUpper: Pair<LevelData, LevelData>?
    var lastZ = -100.0
    var secondsUsed = s

    val list= mutableListOf<Point>()

    while (z >= 0) {
        val p = Point(x, y, z, secondsUsed, true)
        list.add(p)
        // Update ratios each 100 meters altitude
        if (abs(lastZ - z) > 100) {
            currLowerUpper = findLowerUpperLevel(allLevels, z)
            lastZ = z

            if (currLowerUpper != null) {
                val lowLevel = currLowerUpper.first
                val uppLevel = currLowerUpper.second
                xWind = getWindSigmoid(
                    lowLevel,
                    uppLevel,
                    z,
                    lowLevel.vComponentValue,
                    uppLevel.vComponentValue
                )
                yWind = getWindSigmoid(
                    lowLevel,
                    uppLevel,
                    z,
                    lowLevel.uComponentValue,
                    uppLevel.uComponentValue
                )
            }
        }

        vz = (-parachuteVelocityDown) * timeStep
        vx = xWind
        vy = yWind

        x += vx * timeStep
        y += vy * timeStep
        z += vz * timeStep
        secondsUsed += timeStep
    }

    return list
}