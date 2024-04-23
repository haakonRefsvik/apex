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


/*
@Entity(tableName = "threshold")
data class Thresholds(
    val nedbor: String,
    val luftfuktighet: String,
    val vind: String,
    val shearWind: String,
    val duggpunkt: String,
    @PrimaryKey(autoGenerate = true) //sett true
    val id: Int = 1
)
 */