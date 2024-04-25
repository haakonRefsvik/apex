package no.uio.ifi.in2000.rakettoppskytning.model.savedInDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rocketSpecs")
data class RocketSpecs(
    val apogee: String,
    val launchAngle: String,
    val launchDirection: String,
    val thrust: String,
    val burntime: String,
    val dryWeight: String,
    val wetWeight: String,
    val dropTime: String,
    @PrimaryKey(autoGenerate = true) //sett true
    val id: Int = 1
)