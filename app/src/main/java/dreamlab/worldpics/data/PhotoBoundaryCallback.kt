package dreamlab.worldpics.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import dreamlab.worldpics.db.PhotoLocalCache
import dreamlab.worldpics.model.Photo
import dreamlab.worldpics.network.PhotoService
import dreamlab.worldpics.network.searchPhotos

class PhotoBoundaryCallback(
    private val query: String?,
    private val service: PhotoService,
    private val cache: PhotoLocalCache
) : PagedList.BoundaryCallback<Photo>() {

    // keep the last requested page. When the request is successful, increment the page number.
    private var lastRequestedPage = 1

    private val _networkErrors = MutableLiveData<String>()
    // LiveData of network errors.
    val networkErrors: LiveData<String>
        get() = _networkErrors

    // avoid triggering multiple requests in the same time
    private var isRequestInProgress = false

    private fun requestAndSaveData(query: String?) {
        if (isRequestInProgress) return

        isRequestInProgress = true
        searchPhotos(service, query, lastRequestedPage, NETWORK_PAGE_SIZE, { photos ->
            cache.insert(photos) {
                lastRequestedPage++
                isRequestInProgress = false
            }
        }, { error ->
            _networkErrors.postValue(error)
            isRequestInProgress = false
        })
    }

    override fun onZeroItemsLoaded() {
        requestAndSaveData(query)
    }

    override fun onItemAtEndLoaded(itemAtEnd: Photo) {
        requestAndSaveData(query)
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 200
    }
}