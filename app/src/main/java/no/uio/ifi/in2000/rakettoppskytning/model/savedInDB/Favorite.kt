package no.uio.ifi.in2000.rakettoppskytning.model.savedInDB

import androidx.room.Entity
import androidx.room.PrimaryKey


//Legg til slik at bruker for beskjed om han prøver å favorite samme lokasjon flere ganger
//Legge til slik at når bruker trykker på knappen så blir det automatisk hentet data
@Entity(tableName = "favorite")
data class Favorite(
    val name: String,
    val lat: String,
    val lon: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)