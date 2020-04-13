package dreamlab.worldpics.fragment.main.photo.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dreamlab.worldpics.data.resultLiveData
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository module for handling data operations.
 */
@Singleton
class PhotoRepository @Inject constructor(
    private val dao: PhotoDao,
    private val photosRemoteDataSource: PhotoRemoteDataSource
) {

    fun observePagedPhotos(
        connectivityAvailable: Boolean,
        coroutineScope: CoroutineScope
    ) =
        if (connectivityAvailable) observeRemotePagedPhotos(coroutineScope)
        else observeLocalPagedPhotos()

    private fun observeLocalPagedPhotos(): LiveData<PagedList<Photo>> {
        val dataSourceFactory = dao.getPagedPhotos()
        return LivePagedListBuilder(
            dataSourceFactory,
            PhotoPageDataSourceFactory.pagedListConfig()
        ).build()
    }

    private fun observeRemotePagedPhotos(ioCoroutineScope: CoroutineScope)
            : LiveData<PagedList<Photo>> {
        val dataSourceFactory = PhotoPageDataSourceFactory(photosRemoteDataSource, dao, ioCoroutineScope)
        return LivePagedListBuilder(
            dataSourceFactory,
            PhotoPageDataSourceFactory.pagedListConfig()
        ).build()
    }

    fun observePhotos() = resultLiveData(
        databaseQuery = { dao.getPhotos() },
        networkCall = { photosRemoteDataSource.fetchPhotos() },
        saveCallResult = { dao.insertAll(it.hits) })
        .distinctUntilChanged()

    companion object {

        const val PAGE_SIZE = 100

        // For Singleton instantiation
        @Volatile
        private var instance: PhotoRepository? = null

        fun getInstance(dao: PhotoDao, legoSetRemoteDataSource: PhotoRemoteDataSource) =
            instance ?: synchronized(this) {
                instance
                    ?: PhotoRepository(dao, legoSetRemoteDataSource).also { instance = it }
            }
    }
}
