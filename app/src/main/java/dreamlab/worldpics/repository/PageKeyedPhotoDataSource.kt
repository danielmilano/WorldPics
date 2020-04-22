package dreamlab.worldpics.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import dreamlab.worldpics.api.PhotoApi
import dreamlab.worldpics.model.Photo
import dreamlab.worldpics.model.PhotoRequest
import dreamlab.worldpics.util.NetworkLogger
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.Executor

/**
 * A data source that uses the before/after keys returned in page requests.
 * <p>
 * See ItemKeyedSubredditDataSource
 */
class PageKeyedPhotoDataSource(
    private val photoApi: PhotoApi,
    private val request: PhotoRequest?,
    private val retryExecutor: Executor
) : PageKeyedDataSource<Int, Photo>() {

    // keep a function reference for the retry event
    private var retry: (() -> Any)? = null

    /**
     * There is no sync on the state because paging will always call loadInitial first then wait
     * for it to return some success value before calling loadAfter.
     */
    val networkState = MutableLiveData<NetworkState>()

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            retryExecutor.execute {
                it.invoke()
            }
        }
    }

    private fun load() {

    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Photo>) {}

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Photo>
    ) {
        networkState.postValue(NetworkState.LOADING)
        val request = photoApi.searchPhotos(
            page = 1,
            per_page = params.requestedLoadSize,
            query = request?.q,
            orientation = request?.orientation,
            category = request?.category,
            colors = request?.colors,
            editors_choice = request?.top
        )
        NetworkLogger.debug(request)

        try {
            val response = request.execute()
            val items = response.body()?.photos
            retry = null
            networkState.postValue(NetworkState.LOADED)
            callback.onResult(items.orEmpty(), null, 2)
        } catch (ioException: IOException) {
            retry = {
                loadInitial(params, callback)
            }
            networkState.postValue(NetworkState(Status.FAILED, ioException.message))
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Photo>) {
        networkState.postValue(NetworkState.LOADING)
        val request = photoApi.searchPhotos(
            page = params.key,
            per_page = params.requestedLoadSize,
            query = request?.q,
            orientation = request?.orientation,
            category = request?.category,
            colors = request?.colors,
            editors_choice = request?.top
        )
        NetworkLogger.debug(request)

        try {
            val response = request.execute()
            if (response.isSuccessful) {
                val items = response.body()?.photos
                retry = null
                networkState.postValue(NetworkState.LOADED)
                callback.onResult(items.orEmpty(), params.key.inc())
            } else {
                networkState.postValue(NetworkState.LOADED)
                callback.onResult(emptyList(), null)
            }
        } catch (ioException: IOException) {
            retry = {
                loadAfter(params, callback)
            }
            networkState.postValue(NetworkState(Status.FAILED, ioException.message))
        }
    }
}