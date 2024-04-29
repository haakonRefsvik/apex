package no.uio.ifi.in2000.rakettoppskytning.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Favorite
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteCard

@Dao
interface FavoriteCardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteCard(favorite: FavoriteCard)

    @Delete
    suspend fun deleteFavoriteCard(favorite: FavoriteCard)

    @Query("SELECT * FROM favoriteCard")
    fun getFavoriteCards(): Flow<List<FavoriteCard>>


    @Query("DELETE FROM favoriteCard WHERE date < :expiryDate")
    suspend fun removeExpiredCards(expiryDate: String)


}