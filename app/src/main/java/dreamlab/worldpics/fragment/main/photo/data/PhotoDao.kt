package dreamlab.worldpics.fragment.main.photo.data

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*

/**
 * The Data Access Object for the LegoSet class.
 */
@Dao
interface PhotoDao {

    @Query("SELECT * FROM photos")
    fun getPhotos(): LiveData<List<Photo>>

    @Query("SELECT * FROM photos")
    fun getPagedPhotos(): DataSource.Factory<Int, Photo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(photos: List<Photo>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: Photo)

}
