package no.uio.ifi.in2000.rakettoppskytning.model.savedInDB

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "favoriteCard", primaryKeys = ["lat", "lon", "date"])
data class FavoriteCard(
    val lat: String,
    val lon: String,
    val date: String,
)