package no.uio.ifi.in2000.rakettoppskytning.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Favorite

@Database(
    entities = [Favorite::class],
    version = 1
)
abstract class FavoriteDatabase: RoomDatabase() {

    abstract val dao: FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: FavoriteDatabase? = null

        fun getInstance(context: Context): FavoriteDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    FavoriteDatabase::class.java,
                    "favorite.db"
                ).build().also { INSTANCE = it}
            }
        }
    }
}