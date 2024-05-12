package no.uio.ifi.in2000.rakettoppskytning.model.formatting

import android.util.Log
import kotlin.math.abs

fun formatNewValue(
    input: String,
    numberOfIntegers: Int,
    numberOfDecimals: Int,
    highestInput: Double = Double.POSITIVE_INFINITY,
    lowestInput: Double = Double.NEGATIVE_INFINITY,
    oldValue: String = ""
): Double {

    Log.d("inputFormatter", "input: $input")

    if (input == "") {
        return 0.0
    }

    val onlyDigitsAndDot = input.filter { it.isDigit() || it == '.' || it == '-' }
    val oldValueIntegers = oldValue.filter { it.isDigit() || it == '.'}.split(".")[0].length

    val decimalParts = onlyDigitsAndDot.split(".")
    val integerPart = decimalParts.getOrNull(0) ?: ""

    if (integerPart == "") {
        return ("0." + decimalParts[1]).toDouble()
    }

    if (decimalParts.size > 1 && decimalParts[1] == "") {
        Log.d("inputFormatter", "decimal part gone, returning x.0")
        return ("$integerPart.0").toDouble()
    }


    var formattedIntegerValue = integerPart

    while (formattedIntegerValue.filter { it.isDigit() }.length > numberOfIntegers) {
        Log.d("inputFormatter", "Too many integers, dropping the last integer")
        formattedIntegerValue = formattedIntegerValue.dropLast(1)
    }


    var decimalPart = if (decimalParts.size > 1) {
        "." + decimalParts[1]  // Reconstruct the decimal part, if present
    } else {
        ""
    }

    try {
        Log.d("inputFormatter", "old: ${oldValueIntegers}, num-ints: $numberOfIntegers")
        if(oldValueIntegers < numberOfIntegers && (input.toDouble() > highestInput || input.toDouble() < lowestInput)){
            Log.d("inputFormatter", "Not changing the last integer, dropping the last because total is over the limit")
            formattedIntegerValue = formattedIntegerValue.dropLast(1)
        }
    }catch (e: Exception){
        Log.d("inputFormatter", "numberFormatException")
    }


    while (decimalPart.length > numberOfDecimals + 1) {
        Log.d("inputFormatter", "Dropping the last number in the decimal-part")
        decimalPart = decimalPart.dropLast(1)
    }

    var r = (formattedIntegerValue + decimalPart)
    Log.d("inputFormatter", "output: $r")


    if (r.toDouble() > highestInput){
        r = highestInput.toString()
    }

    if (r.toDouble() < lowestInput){
        r = lowestInput.toString()
    }

    Log.d("inputFormatter", "output2: $r")
    return (r).toDouble()
}

fun findClosestDegree(degree: Double): String {
    val degreeToString: Map<Double, String> =
        mapOf(
            Pair(0.0, "North"),
            Pair(45.0, "North-East"),
            Pair(90.0, "East"),
            Pair(135.0, "South-East"),
            Pair(180.0, "South"),
            Pair(225.0, "South-West"),
            Pair(270.0, "West"),
            Pair(315.0, "North-West"),
            Pair(360.0, "North"),
        )

    var closestString = ""
    var shortestDistance = Double.MAX_VALUE

    for ((key, value) in degreeToString) {
        val distance = abs(degree - key)
        if (distance < shortestDistance) {
            shortestDistance = distance
            closestString = value
        }
    }

    if(shortestDistance != 0.0){
        return "$closestString (ish)"
    }

    return closestString
}