package dreamlab.worldpics.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dreamlab.worldpics.model.Photo

/**
 * The Room database for this app
 */
@Database(
    entities = [Photo::class],
    version = 1,
    exportSchema = false
)

abstract class WorldPicsDatabase : RoomDatabase() {

    companion object {

        @Volatile
        private var INSTANCE: WorldPicsDatabase? = null

        fun getInstance(context: Context): WorldPicsDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context): WorldPicsDatabase {
            return Room.databaseBuilder(context, WorldPicsDatabase::class.java, "worldpics-db").build()
        }
    }

    abstract fun photoDao(): PhotoDao
}
