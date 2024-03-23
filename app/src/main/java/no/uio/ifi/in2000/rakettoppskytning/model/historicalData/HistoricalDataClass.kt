package no.uio.ifi.in2000.rakettoppskytning.model.historicalData

import kotlinx.serialization.Serializable

@Serializable
data class HistoricalData(
    /*
    val latitude: Double,
    val longitude: Double,
    val generationtime_ms: Double,
    val utc_offset_seconds: Int,
    val timezone: String,
    val timezone_abbreviation: String,
    val elevation: Double,
    val daily_units: DailyUnits,

     */
    val daily: Daily
)

/*
@Serializable
data class DailyUnits(
    val time: String,
    val precipitation_sum: String,
)

 */


@Serializable
data class Daily(
    val time: List<String>,
    val precipitation_sum: List<Double?>,
)

