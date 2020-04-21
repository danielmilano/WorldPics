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

    private fun getListingPhoto(query: String? = null): Listing<Photo> {

        val dataSourceFactory = PhotoDataSourceFactory(photoApi, query, networkExecutor)

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(INITIAL_NETWORK_PAGE_SIZE)
            .setPageSize(NETWORK_PAGE_SIZE)
            .build()

        val pagedList = LivePagedListBuilder(dataSourceFactory, config)
            .setFetchExecutor(networkExecutor)
            .build()

        return Listing(
            pagedList = pagedList,
            networkState = dataSourceFactory.sourceLiveData.switchMap {
                it.networkState
            },
            retry = {
                dataSourceFactory.sourceLiveData.value?.retryAllFailed()
            }
        )
    }

    fun searchPhotos(query: String): Listing<Photo> {
        Log.d("PhotoRepository", "New query: $query")
        return getListingPhoto(query)
    }

    fun getPhotos(): Listing<Photo> {
        Log.d("PhotoRepository", "Getting all photos")
        return getListingPhoto()
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 200
        private const val INITIAL_NETWORK_PAGE_SIZE = 200
    }

}
