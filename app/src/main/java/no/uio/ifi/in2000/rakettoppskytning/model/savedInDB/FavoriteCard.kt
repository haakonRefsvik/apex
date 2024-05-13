package no.uio.ifi.in2000.rakettoppskytning.model.savedInDB

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
This data class defines a favoriteCard item entity for storage in a database table.*/
@Entity(tableName = "favoriteCard", primaryKeys = ["lat", "lon", "date"])
data class FavoriteCard(
    val lat: String,
    val lon: String,
    val date: String,
)