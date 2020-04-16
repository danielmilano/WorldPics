package dreamlab.worldpics.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import dreamlab.worldpics.api.PhotoApi
import dreamlab.worldpics.model.Photo
import java.util.concurrent.Executor

/**
 * A simple data source factory which also provides a way to observe the last created data source.
 * This allows us to channel its network request status etc back to the UI. See the Listing creation
 * in the Repository class.
 */
class PhotoDataSourceFactory(
    private val photoApi: PhotoApi,
    private val query: String?,
    private val retryExecutor: Executor
) : DataSource.Factory<Int, Photo>() {
    val sourceLiveData = MutableLiveData<PageKeyedPhotoDataSource>()
    override fun create(): DataSource<Int, Photo> {
        val source = PageKeyedPhotoDataSource(photoApi, query, retryExecutor)
        sourceLiveData.postValue(source)
        return source
    }
}
