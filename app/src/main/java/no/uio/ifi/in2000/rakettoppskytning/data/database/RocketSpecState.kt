package no.uio.ifi.in2000.rakettoppskytning.data.database

import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Thresholds

data class RocketSpecState(
    val rocketSpecs: List<Thresholds> = emptyList(),
    val apogee: String = "",
    val launchAngle: String = "",
    val launchDirection: String = "",
    val thrust: String = "",
    val burntime: String = "",
    val dryWeight: String = "",
    val wetWeight: String = "",
    val resolution: String = "",
)