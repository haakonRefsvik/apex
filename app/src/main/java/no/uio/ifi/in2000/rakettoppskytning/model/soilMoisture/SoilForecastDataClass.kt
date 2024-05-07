package no.uio.ifi.in2000.rakettoppskytning.model.soilMoisture

import kotlinx.serialization.Serializable

@Serializable
data class SoilMoisture(
    val daily: Daily
)

@Serializable
data class SoilMoistureHourly(
    val hourly: Hourly
)

@Serializable
data class Hourly(
    val time: List<String>,
    val soil_moisture_0_to_1cm: List<Double>,
)

@Serializable
data class Daily(
    val time: List<String>,
    val precipitation_sum: List<Double?>,
)

