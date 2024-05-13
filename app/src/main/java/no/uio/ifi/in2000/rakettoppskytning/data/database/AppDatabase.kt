package no.uio.ifi.in2000.rakettoppskytning.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Favorite
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.RocketSpecs
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteCard
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Thresholds


/**
 * This code defines a database using Room in Android, specifying its entities and version.
 * provides an instance of a database which allows access to different DAOs
 * */
@Database(
    entities = [Favorite::class, Thresholds::class, FavoriteCard::class, RocketSpecs::class],
    version = 7
)
abstract class AppDatabase: RoomDatabase() {

    abstract val favoriteCardDao: FavoriteCardDao
    abstract val favoriteDao: FavoriteDao
    abstract val thresholdsDao: ThresholdsDao
    abstract val rocketSpecsDao: RocketSpecsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "appDatabase.db"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it}
            }
        }
    }
}

