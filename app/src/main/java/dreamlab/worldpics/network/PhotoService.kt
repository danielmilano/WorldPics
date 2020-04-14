package dreamlab.worldpics.network

import androidx.paging.PagedList
import dreamlab.worldpics.model.Photo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import timber.log.Timber

fun searchPhotos(
    service: PhotoService,
    query: String?,
    page: Int,
    itemsPerPage: Int,
    onSuccess: (photos: PagedList<Photo>) -> Unit,
    onError: (error: String) -> Unit
) {
    Timber.d("query: $query, page: $page, itemsPerPage: $itemsPerPage")

    service.searchPhotos(query = query, page = page, per_page = itemsPerPage).enqueue(
        object : Callback<PhotoSearchResponse> {
            override fun onFailure(call: Call<PhotoSearchResponse>?, t: Throwable) {
                Timber.d("fail to get data")
                onError(t.message ?: "unknown error")
            }

            override fun onResponse(
                call: Call<PhotoSearchResponse>?,
                response: Response<PhotoSearchResponse>
            ) {
                Timber.d("got a response $response")
                if (response.isSuccessful) {
                    onSuccess(response.body()?.photos!!)
                } else {
                    onError(response.errorBody()?.string() ?: "Unknown error")
                }
            }
        }
    )
}

interface PhotoService {

    companion object {
        const val BASE_URL = "https://pixabay.com/"
    }

    @GET("api/?key=8577302-c39c620e7f60e6e6db9dd48d8")
    fun searchPhotos(
        @Query("q") query: String? = null,
        @Query("lang") lang: String? = null,
        @Query("image_type") image_type: String? = null,
        @Query("orientation") orientation: String? = null,
        @Query("category") category: String? = null,
        @Query("min_width") min_width: Int? = null,
        @Query("min_height") min_height: Int? = null,
        @Query("colors") colors: String? = null,
        @Query("editors_choice") editors_choice: Boolean? = null,
        @Query("order") order: String? = null,
        @Query("page") page: Int? = null,
        @Query("per_page") per_page: Int? = null
    ): Call<PhotoSearchResponse>
}
