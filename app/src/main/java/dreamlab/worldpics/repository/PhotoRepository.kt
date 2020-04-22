package dreamlab.worldpics.repository

import androidx.lifecycle.switchMap
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dreamlab.worldpics.api.PhotoApi
import dreamlab.worldpics.model.Photo
import dreamlab.worldpics.model.PhotoRequest
import java.util.concurrent.Executor
import javax.inject.Inject

/**
 * Repository module for handling data operations.
 */
class PhotoRepository @Inject constructor(
    private val photoApi: PhotoApi,
    private val networkExecutor: Executor
) {

    private fun getListingPhoto(request: PhotoRequest? = null): Listing<Photo> {

        val dataSourceFactory = PhotoDataSourceFactory(photoApi, request, networkExecutor)

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
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

    fun searchPhotos(photoRequest: PhotoRequest?): Listing<Photo> {
        return getListingPhoto(photoRequest)
    }

    fun getPhotos(): Listing<Photo> {
        return getListingPhoto()
    }

    companion object {
        private const val INITIAL_NETWORK_PAGE_SIZE = 40
        private const val NETWORK_PAGE_SIZE = 20
    }

}
