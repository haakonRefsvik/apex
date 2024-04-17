package no.uio.ifi.in2000.rakettoppskytning.model.savedInDB


data class ThresholdState(
    val thresholds: List<Thresholds> = emptyList(),
    val nedbor: String = "",
    val luftfuktighet: String = "",
    val vind: String = "",
    val shearWind: String = "",
    val duggpunkt: String = "",
)