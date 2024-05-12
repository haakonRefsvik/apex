package no.uio.ifi.in2000.rakettoppskytning.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy
import androidx.room.Insert
import androidx.room.Query
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Favorite

/** declares functions for database access operations on the Favorite entity.*/
@Dao
interface FavoriteDao {

    /** Inserts a Favorite into the database, replacing it if there's a conflict*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: Favorite)

    /** Deletes a Favorite where name, lat and lon is same as specified name, lat and lon from the database.*/
    @Query("DELETE FROM favorite WHERE name = :name AND lat = :lat AND lon = :lon")
    suspend fun deleteFavorite(name: String, lat: String, lon: String)

    /**  Retrieves a list of Favorite objects from the database asynchronously as a flow*/
    @Query("SELECT * FROM favorite")
    fun getFavorites(): Flow<List<Favorite>>

    /**
    This function retrieves the name of a favorite item by its lat and lon.*/
    @Query("SELECT name FROM favorite WHERE lat = :lat AND lon = :lon")
    fun getFavoriteByLatLon(lat: String, lon: String): String?
}