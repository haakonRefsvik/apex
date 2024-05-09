import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


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
    val fromFtAmsl: String? = null,
    @SerialName("from (m amsl)")
    val fromMAmsl: Int? = null,
    val name: String,
    @SerialName("source_href")
    val sourceHref: String,
    @SerialName("to (fl)")
    val toFl: String? = null,
    @SerialName("to (ft amsl)")
    val toFtAmsl: Int? = null,
    @SerialName("to (m amsl)")
    val mAmsl: Int? = null,
    val opacity: Double? = null,
    val frequency: String? = null,
    @SerialName("notam_only")
    val notamOnly: String? = null,

    )

@Serializable
data class Feature(
    val type: String,
    val properties: Properties,
    val geometry: Geometry
)

@Serializable
data class AirSpace(
    val type: String,
    val features: List<Feature>
)

data class AirSpaceList(
    val list: List<AirSpace>
)
