package dreamlab.worldpics.repository

import android.util.Log
import androidx.lifecycle.switchMap
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dreamlab.worldpics.api.PhotoApi
import dreamlab.worldpics.model.Photo
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
        Log.d("PhotoRepository","New query: $query")

        val dataSourceFactory = PhotoDataSourceFactory(photoApi, query, networkExecutor)

        val pagedListConfig: PagedList.Config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPageSize(NETWORK_PAGE_SIZE)
            .setInitialLoadSizeHint(NETWORK_PAGE_SIZE)
            .build()

        val livePagedList = LivePagedListBuilder(dataSourceFactory, pagedListConfig)
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
        Log.d("PhotoRepository","Getting all photos")
        val dataSourceFactory = PhotoDataSourceFactory(photoApi, null, networkExecutor)

        val pagedListConfig: PagedList.Config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(INITIAL_NETWORK_PAGE_SIZE)
            .setPageSize(NETWORK_PAGE_SIZE)
            .build()

        val livePagedList = LivePagedListBuilder(dataSourceFactory, pagedListConfig)
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
        private const val NETWORK_PAGE_SIZE = 50
        private const val INITIAL_NETWORK_PAGE_SIZE = 150
    }


}
