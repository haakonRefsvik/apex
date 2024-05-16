package no.uio.ifi.in2000.rakettoppskytning.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteCard

/**
 * Declares a function for database access operation on FavoriteCard entity
 */
@Dao
interface FavoriteCardDao {

    /** Inserts FavoriteCard in the database and replacing in case of a conflict*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteCard(favorite: FavoriteCard)

    /** Deletes a specified FavoriteCard from the database*/
    @Delete
    suspend fun deleteFavoriteCard(favorite: FavoriteCard)

    /** Retrieves a list of FavoriteCard objects from the database asynchronously as a flow.*/
    @Query("SELECT * FROM favoriteCard")
    fun getFavoriteCards(): Flow<List<FavoriteCard>>


    /** Deletes FavoriteCard objects from the database where the date is earlier than a specified expiryDate.*/
    @Query("DELETE FROM favoriteCard WHERE date < :expiryDate")
    suspend fun removeExpiredCards(expiryDate: String)
}