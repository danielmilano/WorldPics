package dreamlab.worldpics.fragment.main.photo.data

import dreamlab.worldpics.network.BaseDataSource
import dreamlab.worldpics.network.service.PhotoService
import javax.inject.Inject

class PhotoRemoteDataSource @Inject constructor(private val service: PhotoService) :
    BaseDataSource() {

    suspend fun fetchPhotos(
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
        per_page: Int? = null
    ) = getResult {
        service.getPhotos(
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
    }
}
