package no.uio.ifi.in2000.rakettoppskytning.model.savedInDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "threshold")
data class Thresholds(
    val percipitation: String,
    val humidity: String,
    val wind: String,
    val shearWind: String,
    val dewpoint: String,
    @PrimaryKey(autoGenerate = true) //sett true
    val id: Int = 1
)
