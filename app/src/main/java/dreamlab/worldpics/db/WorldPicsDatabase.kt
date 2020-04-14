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
    version = 1,
    exportSchema = false
)

abstract class WorldPicsDatabase : RoomDatabase() {

    abstract fun photoDao(): PhotoDao

    companion object {

        @Volatile
        private var INSTANCE: WorldPicsDatabase? = null

        fun getInstance(context: Context): WorldPicsDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context): WorldPicsDatabase {
            return Room.databaseBuilder(context, WorldPicsDatabase::class.java, "worldpics-db")
                /*.addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>().build()
                        WorkManager.getInstance(context).enqueue(request)
                    }
                })*/
                .build()
        }
    }
}
