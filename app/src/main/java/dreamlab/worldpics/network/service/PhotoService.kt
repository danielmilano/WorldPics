package dreamlab.worldpics.network.service

import dreamlab.worldpics.network.response.SearchResultsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PhotoService {

    companion object {
        const val BASE_URL = "https://pixabay.com/"
    }

    @GET("api/?key=8577302-c39c620e7f60e6e6db9dd48d8")
    suspend fun getPhotos(   @Query("q") query: String? = null,
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
    ): Response<SearchResultsResponse>
}
