package no.uio.ifi.in2000.rakettoppskytning.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.RocketSpecs

/**
 * This DAO interface declares functions for inserting
 * and updating rocket specifications in the database,
 * as well as retrieving rocket specifications by their ID as a Flow.
 * */
@Dao
interface RocketSpecsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRocketSpecs(rocketSpecs: RocketSpecs)
    @Update
    suspend fun updateRocketSpecs(rocketSpecs: RocketSpecs)

    @Query("SELECT * FROM rocketSpecs WHERE id = :id")
    fun getRocketSpecsById(id: Int): Flow<RocketSpecs?>
}