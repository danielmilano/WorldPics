package dreamlab.worldpics.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import dreamlab.worldpics.api.PhotoApi
import dreamlab.worldpics.model.Photo
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
    private val query: String?,
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

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Photo>) {}

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Photo>) {
        val page = params.key
        networkState.postValue(NetworkState.LOADING)
        photoApi.searchPhotos(query = query, page = page, per_page = DEFAULT_NETWORK_PAGE_SIZE)
            .enqueue(
                object : retrofit2.Callback<PhotoApi.PhotoSearchResponse> {
                    override fun onFailure(call: Call<PhotoApi.PhotoSearchResponse>, t: Throwable) {
                        retry = {
                            loadAfter(params, callback)
                        }
                        networkState.postValue(NetworkState.error(t.message ?: "unknown err"))
                    }

                    override fun onResponse(
                        call: Call<PhotoApi.PhotoSearchResponse>,
                        response: Response<PhotoApi.PhotoSearchResponse>
                    ) {
                        if (response.isSuccessful) {
                            val data = response.body()?.photos
                            val items = data.orEmpty()
                            retry = null
                            callback.onResult(items, page + 1)
                            networkState.postValue(NetworkState.LOADED)
                        } else {
                            retry = {
                                loadAfter(params, callback)
                            }
                            networkState.postValue(
                                NetworkState.error("error code: ${response.code()}")
                            )
                        }
                    }
                }
            )
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Photo>
    ) {
        val request = photoApi.searchPhotos(
            query = query,
            page = 1,
            per_page = DEFAULT_NETWORK_PAGE_SIZE
        )
        networkState.postValue(NetworkState.LOADING)

        try {
            val response = request.execute()
            val data = response.body()?.photos
            val items = data?.map { it } ?: emptyList()
            retry = null
            networkState.postValue(NetworkState.LOADED)
            callback.onResult(items, null, 2)
        } catch (ioException: IOException) {
            retry = {
                loadInitial(params, callback)
            }
            val error = NetworkState.error(ioException.message ?: "unknown error")
            networkState.postValue(error)
        }
    }

    companion object {
        private const val DEFAULT_NETWORK_PAGE_SIZE = 20
    }
}