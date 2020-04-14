package dreamlab.worldpics.db

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.room.*
import dreamlab.worldpics.model.Photo

/**
 * The Data Access Object for the LegoSet class.
 */
@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(photos: ArrayList<Photo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(photo: Photo)

    @Query("SELECT * FROM photos")
    fun photos(): DataSource.Factory<Int, Photo>

    @Query("SELECT * FROM photos WHERE (color LIKE :color)")
    fun photosByColor(color: String): DataSource.Factory<Int, Photo>

    @Query("SELECT * FROM photos WHERE (tags LIKE :query)")
    fun photosByQuery(query: String): DataSource.Factory<Int, Photo>

}
