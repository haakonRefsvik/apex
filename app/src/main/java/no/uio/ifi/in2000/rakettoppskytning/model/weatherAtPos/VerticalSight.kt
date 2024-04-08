package no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos

import kotlin.math.roundToInt


fun roundToNearestHundred(number: Double): Int {
    return (number * 10).toInt() * 100
}


fun getVerticalSightKmNumber(
    fogGround: Double,
    cloudLow: Double,
    cloudMed: Double,
    cloudHigh: Double
): Double {

    val l2 = (fogGround + cloudLow).coerceAtMost(100.0) // 10 + 20
    val l3 = (l2 + cloudMed).coerceAtMost(100.0)        // 30 +
    val l4 = (l3 + cloudHigh).coerceAtMost(100.0)

    val m1 = ((100 - fogGround) / 100) * 1
    val m2 = ((100 - l2) / 100) * 1
    val m3 = ((100 - l3) / 100) * 3
    val m4 = ((100 - l4) / 100) * 5

    return m1 + m2 + m3 + m4
}

fun getVerticalSightKm(
    fogGround: Double,
    cloudLow: Double,
    cloudMed: Double,
    cloudHigh: Double
): String {

    val sumKm = getVerticalSightKmNumber(fogGround, cloudLow, cloudMed, cloudHigh)

    if (sumKm > 10) {
        return ">10 km"
    }

    if (sumKm < 0.1) {
        return "<100 m"
    }

    if (sumKm < 1) {
        return "≈${(roundToNearestHundred(sumKm))} m"
    }

    return "≈${sumKm.roundToInt()} km"
}

