package no.uio.ifi.in2000.rakettoppskytning

import kotlinx.serialization.Serializable

@Serializable
data class FlyDataClass(
    val time: Int,
    val states: List<FlyData>
)
@Serializable
data class FlyData(
    val icao24:String?,
    val callsign:String?,
    val origin_country:String?,
    val time_position:Int?,
    val last_contact: Int?,
    val longitude: Float?,
    val latitude: Float?,
    val baro_altitude: Float?,
    val on_ground: Boolean?,
    val velocity: Float?,
    val true_track: Float?,
    val vertical_rate: Float?,
    val sensors: List<Int>?,
    val geo_altitude: Float?,
    val squawk: String?,
    val spi: Boolean?,
    val position_source: Int?,
    val category: Int?,

)


