package no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.soil


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
        soilPercentage < 10 -> "Svært høy brannfare"
        soilPercentage < 30 -> "Moderat brannfare"
        soilPercentage < 60 -> "Lavere brannfare"
        soilPercentage < 90 -> "Lav brannfare"
        soilPercentage >= 90 -> "Svært lav brannfare"
        else -> ""
    }
}

fun getSoilCategory(soilPercentage: Int): String{
    return when {
        soilPercentage < 10 -> "Bakken er svært tørr"
        soilPercentage < 30 -> "Bakken er tørr"
        soilPercentage < 60 -> "Bakken er middels fuktig"
        soilPercentage < 90 -> "Bakken er ganske fuktig"
        soilPercentage >= 90 -> "Bakken er svært fuktig"
        else -> ""
    }
}