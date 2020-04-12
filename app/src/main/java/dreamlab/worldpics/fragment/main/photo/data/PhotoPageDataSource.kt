package dreamlab.worldpics.fragment.main.photo.data

import androidx.paging.PageKeyedDataSource
import com.elifox.legocatalog.data.Result
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Data source for lego sets pagination via paging library
 */
class PhotoPageDataSource
@Inject constructor(
    private val dataSource: PhotoRemoteDataSource,
    private val dao: PhotoDao,
    private val scope: CoroutineScope
) : PageKeyedDataSource<Int, Photo>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Photo>
    ) {
        fetchData(page = 0, per_page = params.requestedLoadSize) {
            callback.onResult(it, null, 2)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Photo>) {
        val page = params.key
        fetchData(page = page, per_page = params.requestedLoadSize) {
            callback.onResult(it, page + 1)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Photo>) {
        val page = params.key
        fetchData(page = page, per_page = params.requestedLoadSize) {
            callback.onResult(it, page - 1)
        }
    }

    private fun fetchData(
        query: String? = null,
        lang: String? = null,
        image_type: String? = null,
        orientation: String? = null,
        category: String? = null,
        min_width: Int? = null,
        min_height: Int? = null,
        colors: String? = null,
        editors_choice: Boolean? = null,
        order: String? = null,
        page: Int? = null,
        per_page: Int? = null, callback: (List<Photo>) -> Unit
    ) {
        scope.launch(getJobErrorHandler()) {
            val response = dataSource.fetchPhotos(
                query,
                lang,
                image_type,
                orientation,
                category,
                min_width,
                min_height,
                colors,
                editors_choice,
                order,
                page,
                per_page
            )
            if (response.status == Result.Status.SUCCESS) {
                val results = response.data!!.hits
                dao.insertAll(results)
                callback(results!!)
            } else if (response.status == Result.Status.ERROR) {
                postError(response.message!!)
            }
        }
    }

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        postError(e.message ?: e.toString())
    }

    private fun postError(message: String) {
        Timber.e("An error happened: $message")
    }

}