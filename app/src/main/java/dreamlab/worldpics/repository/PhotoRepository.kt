package dreamlab.worldpics.repository

import androidx.lifecycle.switchMap
import androidx.paging.LivePagedListBuilder
import dreamlab.worldpics.api.PhotoApi
import dreamlab.worldpics.model.Photo
import timber.log.Timber
import java.util.concurrent.Executor
import javax.inject.Inject

/**
 * Repository module for handling data operations.
 */
class PhotoRepository @Inject constructor(
    private val photoApi: PhotoApi,
    private val networkExecutor: Executor
) {

    /**
     * Search repositories whose names match the query.
     */
    fun searchPhotos(query: String): Listing<Photo> {
        Timber.d("New query: $query")

        val dataSourceFactory = PhotoDataSourceFactory(photoApi, query, networkExecutor)

        val livePagedList = LivePagedListBuilder(dataSourceFactory, NETWORK_PAGE_SIZE)
            .setFetchExecutor(networkExecutor)
            .build()

        return Listing(
            pagedList = livePagedList,
            networkState = dataSourceFactory.sourceLiveData.switchMap {
                it.networkState
            },
            retry = {
                dataSourceFactory.sourceLiveData.value?.retryAllFailed()
            }
        )
    }

    fun getPhotos(): Listing<Photo> {
        Timber.d("Getting all photos")

        val dataSourceFactory = PhotoDataSourceFactory(photoApi, null, networkExecutor)

        val livePagedList = LivePagedListBuilder(dataSourceFactory, NETWORK_PAGE_SIZE)
            .setFetchExecutor(networkExecutor)
            .build()

        return Listing(
            pagedList = livePagedList,
            networkState = dataSourceFactory.sourceLiveData.switchMap {
                it.networkState
            },
            retry = {
                dataSourceFactory.sourceLiveData.value?.retryAllFailed()
            }
        )
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 100
    }


}
