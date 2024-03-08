package no.uio.ifi.in2000.rakettoppskytning

import kotlinx.serialization.Serializable

@Serializable
data class FlyDataClass(
    val time: Int,
    val states: List<FlyData>
)
@Serializable
data class FlyData(
    val icao24: String?,
    val callsign: String?,
    val origin_country: String?,
    val time_position: Int?,
    val last_contact: Int?,
    val longitude: Double,
    val latitude: Double,
    val baro_altitude: Double,
    val on_ground: Boolean?,
    val velocity: Double,
    val true_track: Double,
    val vertical_rate: Int,
    val sensors: List<Int>?,
    val geo_altitude: Double,
    val squawk: String?,
    val spi: Boolean?,
    val position_source: Int?,

    )


