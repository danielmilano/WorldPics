package dreamlab.worldpics.db

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import dreamlab.worldpics.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

@Dao
interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(photo: Photo)

    @Query("delete FROM photos WHERE (id == :id)")
    fun deletePhoto(id: String)

    @Query("SELECT * FROM photos")
    fun photos(): LiveData<List<Photo>>?
}
