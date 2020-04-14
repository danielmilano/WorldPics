package dreamlab.worldpics.db

import androidx.paging.DataSource
import dreamlab.worldpics.model.Photo
import timber.log.Timber
import java.util.concurrent.Executor
import javax.inject.Inject

class PhotoLocalCache @Inject constructor(
    private val photoDao: PhotoDao,
    private val ioExecutor: Executor
) {

    /**
     * Insert a list of repos in the database, on a background thread.
     */
    fun insert(photos: ArrayList<Photo>, insertFinished: () -> Unit) {
        ioExecutor.execute {
            Timber.d("Inserting ${photos.size} photos")
            photoDao.insertAll(photos)
            insertFinished()
        }
    }

    fun photosByQuery(query: String): DataSource.Factory<Int, Photo> {
        return photoDao.photosByQuery(query)
    }

    fun photoByColor(color: String): DataSource.Factory<Int, Photo> {
        return photoDao.photosByColor(color)
    }

    fun photos(): DataSource.Factory<Int, Photo> {
        return photoDao.photos()
    }
}