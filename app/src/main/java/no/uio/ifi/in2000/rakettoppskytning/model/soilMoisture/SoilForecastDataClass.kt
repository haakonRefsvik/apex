package no.uio.ifi.in2000.rakettoppskytning.model.soilMoisture

import kotlinx.serialization.Serializable


@Serializable
data class SoilMoistureHourly(
    val hourly: Hourly
)

@Serializable
data class Hourly(
    val time: List<String>,
    val soil_moisture_0_to_1cm: List<Double>,
)