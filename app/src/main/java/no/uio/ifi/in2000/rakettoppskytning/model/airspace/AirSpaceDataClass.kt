import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class AirSpaceList(val list: List<AirSpace>)

@Serializable
data class Geometry(
    val type: String,
    val coordinates: List<List<List<Double>>>,
)

@Serializable
data class Properties(
    @SerialName("class")
    val class_field: String,
    val color: String,
    val country: String,
    val fillColor: String? = null,
    val fillOpacity: Double,
    @SerialName("from (fl)")
    val fromFl: String? = null,
    @SerialName("from (ft amsl)")
    val fromFtAmsl: Double? = null, //Any
    @SerialName("from (m amsl)")
    val fromMAmsl: Double? = null, //Any
    val name: String,
    @SerialName("source_href")
    val sourceHref: String,
    @SerialName("to (fl)")
    val toFl: String? = null,
    @SerialName("to (ft amsl)")
    val toFtAmsl: Double? = null, //Any
    @SerialName("to (m amsl)")
    val toMAmsl: Double? = null, //Any
    val opacity: Double? = null,
    val frequency: String? = null,
    @SerialName("notam_only")
    val notamOnly: String? = null,
)

@Serializable
data class feature(
    val type: String,
    val properties: Properties,
    val geometry: Geometry
)

@Serializable
data class AirSpace(
    val type: String,
    val features: List<feature>
)
