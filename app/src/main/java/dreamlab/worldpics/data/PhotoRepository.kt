package dreamlab.worldpics.data

import androidx.paging.LivePagedListBuilder
import dreamlab.worldpics.db.PhotoLocalCache
import dreamlab.worldpics.model.PhotoSearchResult
import dreamlab.worldpics.network.PhotoService
import timber.log.Timber
import javax.inject.Inject

/**
 * Repository module for handling data operations.
 */
class PhotoRepository @Inject constructor(
    private val service: PhotoService,
    private val cache: PhotoLocalCache
) {

    /**
     * Search repositories whose names match the query.
     */
    fun searchPhotos(query: String): PhotoSearchResult {
        Timber.d("New query: $query")

        // Get data source factory from the local cache
        val dataSourceFactory = cache.photosByQuery(query)

        // Construct the boundary callback
        val boundaryCallback = PhotoBoundaryCallback(query, service, cache)
        val networkErrors = boundaryCallback.networkErrors

        // Get the paged list
        val data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
            .setBoundaryCallback(boundaryCallback)
            .build()

        // Get the network errors exposed by the boundary callback
        return PhotoSearchResult(data, networkErrors)
    }

    fun getPhotos(): PhotoSearchResult {
        Timber.d("Getting all photos")

        // Get data source factory from the local cache
        val dataSourceFactory = cache.photos()

        // Construct the boundary callback
        val boundaryCallback = PhotoBoundaryCallback(null, service, cache)
        val networkErrors = boundaryCallback.networkErrors

        // Get the paged list
        val data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
            .setBoundaryCallback(boundaryCallback)
            .build()

        // Get the network errors exposed by the boundary callback
        return PhotoSearchResult(data, networkErrors)
    }

    companion object {
        private const val DATABASE_PAGE_SIZE = 100
    }

}
