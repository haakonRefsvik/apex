package no.uio.ifi.in2000.rakettoppskytning.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Favorite

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: Favorite)

    @Query("DELETE FROM favorite WHERE name = :name AND lat = :lat AND lon = :lon")
    suspend fun deleteFavorite(name: String, lat: String, lon: String)

    @Query("SELECT * FROM favorite")
    fun getFavorites(): Flow<List<Favorite>>

    @Query("SELECT name FROM favorite WHERE lat = :lat AND lon = :lon")
    fun getFavoriteByLatLon(lat: String, lon: String): String?
}