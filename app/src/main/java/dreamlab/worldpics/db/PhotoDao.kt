package dreamlab.worldpics.db

import androidx.lifecycle.LiveData
import androidx.room.*
import dreamlab.worldpics.model.Photo

@Dao
interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: Photo)

    @Query("delete FROM photos WHERE (id == :id)")
    suspend fun deletePhoto(id: String)

    @Query("SELECT * FROM photos")
    fun photos(): LiveData<List<Photo>>?

    @Query("SELECT * FROM photos")
    fun getAll(): List<Photo>

    @Query("SELECT * FROM photos WHERE (id == :id)")
    suspend fun getPhotoById(id: String): Photo?
}
