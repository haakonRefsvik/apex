package no.uio.ifi.in2000.rakettoppskytning.model.savedInDB

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
This data class defines a threshold item entity for storage in a database table.*/
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
