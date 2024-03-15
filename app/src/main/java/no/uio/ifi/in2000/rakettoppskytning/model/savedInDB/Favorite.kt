package no.uio.ifi.in2000.rakettoppskytning.model.savedInDB

import androidx.room.Entity
import androidx.room.PrimaryKey


//oppdater databasen hver time eller hvor mange timer som met oppdaterer
//inject data fra apiet på databasen

@Entity
data class Favorite(
    val name: String,
    val lat: String,
    val lon: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
