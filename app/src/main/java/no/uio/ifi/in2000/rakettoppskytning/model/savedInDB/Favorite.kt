package no.uio.ifi.in2000.rakettoppskytning.model.savedInDB

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
This data class defines a favorite item entity for storage in a database table.*/
@Entity(tableName = "favorite")
data class Favorite(
    val name: String,
    val lat: String,
    val lon: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)