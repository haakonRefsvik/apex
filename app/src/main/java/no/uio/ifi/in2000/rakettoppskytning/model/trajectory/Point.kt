package no.uio.ifi.in2000.rakettoppskytning.model.trajectory

import kotlin.math.roundToInt


/**
 *
 * This Kotlin class defines a 3D point with coordinates (x, y, z), time,
 * and a boolean indicating if it's parachuted, with methods to retrieve these values.
 * */
class Point(
    private val x: Double,
    private val y: Double,
    private val z: Double,
    private val timeSeconds: Double,
    private val parachuted: Boolean = false
) {
    override fun toString(): String {
        return "(x: ${x.roundToInt()}, y: ${y.roundToInt()}, z: ${z.roundToInt()}, time: ${timeSeconds.toInt()}, parachuted: $parachuted)"
    }

    fun getX(): Double {
        return x
    }

    fun getY(): Double {
        return y
    }

    fun getZ(): Double {
        return z
    }

    fun isParachuted(): Boolean {
        return parachuted
    }

}