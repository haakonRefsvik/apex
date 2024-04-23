package no.uio.ifi.in2000.rakettoppskytning.model.savedInDB

import androidx.room.Entity
import androidx.room.PrimaryKey

sealed interface RocketSpecsEvent {
    object SaveRocketSpecs: RocketSpecsEvent
    data class SetApogee(val apogee: String): RocketSpecsEvent
    data class SetLauncAngle(val launchAngle: String): RocketSpecsEvent
    data class SetLaunchDirection(val launchDirection: String): RocketSpecsEvent
    data class SetThrust(val thrust: String): RocketSpecsEvent
    data class SetBurntime(val burntime: String): RocketSpecsEvent
    data class SetDryWeight(val dryWeight: String): RocketSpecsEvent
    data class SetWetWeight(val wetWeight: String): RocketSpecsEvent
    data class SetDropTime(val dropTime: String): RocketSpecsEvent
}
