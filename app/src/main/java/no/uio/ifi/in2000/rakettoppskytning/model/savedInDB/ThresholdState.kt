package no.uio.ifi.in2000.rakettoppskytning.model.savedInDB


data class ThresholdState(
    val thresholds: List<Thresholds> = emptyList(),
    val percipitation: String = "",
    val humidity: String = "",
    val wind: String = "",
    val shearWind: String = "",
    val dewpoint: String = "",
)