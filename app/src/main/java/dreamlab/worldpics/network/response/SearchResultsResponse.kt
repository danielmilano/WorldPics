package dreamlab.worldpics.network.response

import com.google.gson.annotations.SerializedName
import dreamlab.worldpics.fragment.main.photo.data.Photo

data class SearchResultsResponse(
    @SerializedName("total")
    var total: Int? = null,

    @SerializedName("totalHits")
    var totalHits: Int? = null,

    @SerializedName("hits")
    var hits: List<Photo>? = null
)
