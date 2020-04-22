package dreamlab.worldpics.api

import com.google.gson.annotations.SerializedName
import dreamlab.worldpics.model.Photo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PhotoApi {

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

    class PhotoSearchResponse(
        var total: Int,
        var totalHits: Int,
        @SerializedName("hits")
        val photos: ArrayList<Photo>
    )

    companion object {
        const val BASE_URL = "https://pixabay.com/"
    }
}
