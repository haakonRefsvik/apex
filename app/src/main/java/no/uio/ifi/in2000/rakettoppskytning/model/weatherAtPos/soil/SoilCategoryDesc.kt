package no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.soil

import no.uio.ifi.in2000.rakettoppskytning.R


/**
 * From chat-GPT:
 * In general, soil moisture levels are often categorized into different ranges:
 *
 * Very Dry/Low: Soil moisture levels are critically low, and plants may experience water stress.
 * This range often corresponds to soil moisture levels below 10% or 0.10 m^3/m^3.
 *
 * Dry/Moderate: Soil moisture levels are somewhat low, but plants can still survive.
 * This range typically falls between 10% and 30% or 0.10 to 0.30 m^3/m^3.
 *
 * Moist/Optimal: Soil moisture levels are sufficient for healthy plant growth, and water stress is minimal.
 * This range often falls between 30% and 70% or 0.30 to 0.70 m^3/m^3.
 *
 * Wet/High: Soil moisture levels are relatively high, and drainage may become an issue.
 * This range typically falls above 70% or 0.70 m^3/m^3.
 *
 * */
fun getSoilDescription(soilPercentage: Int): String{
    return when {
        soilPercentage < 10 -> "Very high fire risk"
        soilPercentage < 15 -> "High fire risk"
        soilPercentage < 30 -> "Moderate fire risk"
        soilPercentage < 60 -> "Lower fire risk"
        soilPercentage < 90 -> "Low fire risk"
        soilPercentage >= 90 -> "Very low fire risk"
        else -> ""
    }
}

fun getSoilCategory(soilPercentage: Int): String{
    return when {
        soilPercentage < 10 -> "The ground is very dry"
        soilPercentage < 30 -> "The ground is dry"
        soilPercentage < 60 -> "The ground is moderately dry"
        soilPercentage < 90 -> "The ground is pretty moist"
        soilPercentage >= 90 -> "The ground is very moist"
        else -> ""
    }
}

fun getSoilScore(soilPercentage: Int?): Double{
    if (soilPercentage == null){
        return -1.0
    }

    return if(soilPercentage < 15){
        1.0
    }
    else{
        -1.0
    }
}