package no.uio.ifi.in2000.rakettoppskytning.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Thresholds

/**
 * This DAO interface manages threshold data,
 * offering functions to insert and update threshold values in the database,
 * and to retrieve threshold data by ID as a Flow.*/
@Dao
interface ThresholdsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThresholds(thresholds: Thresholds)

    @Update
    suspend fun updateThreshold(thresholds: Thresholds)

    @Query("SELECT * FROM threshold WHERE id = :id")
    fun getThresholdById(id: Int): Flow<Thresholds?>
}