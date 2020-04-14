package dreamlab.worldpics.network

import com.google.gson.annotations.SerializedName
import dreamlab.worldpics.model.Photo

data class PhotoSearchResponse(
    @SerializedName("total")
    var total: Int,

    @SerializedName("totalHits")
    var totalHits: Int,

    @SerializedName("hits")
    val photos: ArrayList<Photo>
)
