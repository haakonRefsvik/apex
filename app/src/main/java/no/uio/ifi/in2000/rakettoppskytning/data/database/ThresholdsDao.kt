package no.uio.ifi.in2000.rakettoppskytning.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Thresholds

@Dao
interface ThresholdsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThresholds(thresholds: Thresholds)

    @Update
    suspend fun updateThreshold(thresholds: Thresholds)


    @Query("SELECT * FROM threshold WHERE id = :id")
    fun getThresholdById(id: Int): Flow<Thresholds?>
}