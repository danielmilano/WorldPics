package dreamlab.worldpics.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dreamlab.worldpics.worker.SeedDatabaseWorker
import dreamlab.worldpics.model.Photo

/**
 * The Room database for this app
 */
@Database(
    entities = [Photo::class],
    version = 1, exportSchema = false
)

abstract class WorldPicsDatabase : RoomDatabase() {

    abstract fun photoDao(): PhotoDao

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: WorldPicsDatabase? = null

        fun getInstance(context: Context): WorldPicsDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase(
                        context
                    )
                        .also { instance = it }
            }
        }

        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): WorldPicsDatabase {
            return Room.databaseBuilder(context, WorldPicsDatabase::class.java, "worldpics-db")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>().build()
                        WorkManager.getInstance(context).enqueue(request)
                    }
                })
                .build()
        }
    }
}
