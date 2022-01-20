package dreamlab.worldpics.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey
    @SerializedName("id")
    var id: String,

    @SerializedName("pageURL")
    var pageURL: String? = null,

    @SerializedName("type")
    var type: String? = null,

    @SerializedName("tags")
    var tags: String? = null,

    @SerializedName("previewURL")
    var previewURL: String? = null,

    @SerializedName("color")
    var color: String? = null,

    @SerializedName("previewHeight")
    var previewHeight: String? = null,

    @SerializedName("webformatURL")
    var webformatURL: String? = null,

    @SerializedName("webformatWidth")
    var webformatWidth: String? = null,

    @SerializedName("webformatHeight")
    var webformatHeight: String? = null,

    @SerializedName("largeImageURL")
    var largeImageURL: String? = null,

    @SerializedName("fullHDURL")
    var fullHDURL: String? = null,

    @SerializedName("imageURL")
    var imageURL: String? = null,

    @SerializedName("imageWidth")
    var imageWidth: String? = null,

    @SerializedName("imageHeight")
    var imageHeight: String? = null,

    @SerializedName("imageSize")
    var imageSize: String? = null,

    @SerializedName("views")
    var views: Int? = null,
    @SerializedName("downloads")
    var downloads: Int? = null,

    @SerializedName("favorites")
    var favorites: Int? = null,

    @SerializedName("likes")
    var likes: Int? = null,

    @SerializedName("comments")
    var comments: Int? = null,

    @SerializedName("user_id")
    var user_id: Int? = null,

    @SerializedName("user")
    var user: String? = null,

    @SerializedName("userImageURL")
    var userImageURL: String? = null,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var imageBlob: ByteArray? = null,
) : Serializable
