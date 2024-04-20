package no.uio.ifi.in2000.rakettoppskytning.data.ballistic

import no.uio.ifi.in2000.rakettoppskytning.model.grib.LevelData
import java.lang.Math.pow
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class Point(val x: Double, val y: Double, val z: Double) {
    override fun toString(): String {
        return "(x: $x, y: $y, z: $z)"
    }
}

/**Considered alt(itude) is between lower and upperlayer, calculate the ratio between the two.
 * The returning pair consists of two doubles that add up to 1 like (lower, upper) */
fun getLevelRatios(lowerAlt: Double, upperAlt: Double, alt: Double): Pair<Double, Double>?{
    val altBetweenLayers = upperAlt - lowerAlt

    if(alt > upperAlt || alt < lowerAlt){
        return null
    }

    val d = alt - lowerAlt
    val p1 = d / altBetweenLayers

    return Pair(1 - p1, p1)
}

fun mergeLevelData(ratios: Pair<Double, Double>, lowerData: Double, upperData: Double): Double{
    return (ratios.first * lowerData) + (ratios.second * upperData)
}

fun findLowerUpperLevel(allLevels: List<LevelData>, altitude: Double): Pair<LevelData, LevelData>?{
    val sortedLevels = allLevels.sortedByDescending { it.pressurePascal }

    sortedLevels.forEachIndexed { index, _ ->
        val l = sortedLevels[index]
        if(index == sortedLevels.lastIndex){
            return Pair(l, l)
        }
        val u = sortedLevels[index + 1]

        if (l.getLevelHeightInMeters() <= altitude && altitude <= u.getLevelHeightInMeters() ) {
            return Pair(l, u)
        }
    }

    return null
}
fun simulateTrajectory(
    burnTime: Double,
    launchAngle: Double,
    launchDir: Double,
    altitude: Double,
    thrust: Double,
    apogee: Double,
    mass: Double,
    dt: Double,
    levelData: HashMap<Double, LevelData>,
    vAfterParachute: Double = 8.6
): List<Point>{
    val g = 9.81
    val rho = 1.225
    val cd = 0.5
    val launchAngle_rad = Math.toRadians(launchAngle)
    val launchDir_rad = Math.toRadians(launchDir)

    var x = 0.0
    var y = 0.0
    var z: Double = altitude

    var vx = 0.0
    var vy = 0.0
    var vz = 0.0

    var secondsUsed = 0.0
    var parachuteDeployed = false
    var timeStep = dt

    var burnTimeLeft = burnTime
    var ax: Double
    var ay: Double
    var az: Double

    val list= mutableListOf<Point>()

    while (z >= altitude){
        if(parachuteDeployed){
            timeStep = 1.0  // only calculate each second after parachute is deployed
        }
        secondsUsed += timeStep

        if(burnTimeLeft >= 0 && z <= apogee){
            ax = thrust * cos(launchAngle_rad) * sin(launchDir_rad) / mass
            ay = thrust * cos(launchAngle_rad) * cos(launchDir_rad) / mass
            az = thrust * sin(launchAngle_rad) / mass - g
            burnTimeLeft -= timeStep
        }
        else{
            ax = 0.0
            ay = 0.0
            az = -g

            val v = sqrt(vx.pow(2.0) + vy.pow(2.0) + vz.pow(2.0))
            val fDragX = -0.5 * rho * cd * v * vx
            val fDragY = -0.5 * rho * cd * v * vy
            val fDragZ = -0.5 * rho * cd * v * vz

            ax += fDragX / mass
            ay += fDragY / mass
            az += fDragZ / mass
        }

        vx += ax * timeStep
        vy += ay * timeStep
        vz += az * timeStep

        if(parachuteDeployed){
            vz = ( - vAfterParachute ) * timeStep
            vx = 0.0    // WINDDATA HERE
            vy = 0.0
        }

        x += vx * timeStep
        y += vy * timeStep
        z += vz * timeStep

        if(vz < 0 && !parachuteDeployed){
            parachuteDeployed = true
        }

        if(z > apogee){
            z = apogee
        }

        val p = Point(x, y, z)
        println(p)
        list.add(p)
    }

    return list
}

