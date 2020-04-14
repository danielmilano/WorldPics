package dreamlab.worldpics.ui.photo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import dreamlab.worldpics.data.PhotoRepository
import dreamlab.worldpics.model.Photo
import dreamlab.worldpics.model.PhotoSearchResult
import javax.inject.Inject

class PhotoViewModel @Inject constructor(private val repository: PhotoRepository) : ViewModel() {

    private val queryLiveData = MutableLiveData<String?>()
    private val photosResult: LiveData<PhotoSearchResult> = Transformations.map(queryLiveData) {
        it?.let {
            repository.searchPhotos(it)
        } ?: kotlin.run {
            repository.getPhotos()
        }
    }

    val photos: LiveData<PagedList<Photo?>> = Transformations.switchMap(photosResult) { it.data }
    val networkErrors: LiveData<String> = Transformations.switchMap(photosResult) {
        it.networkErrors
    }

    /**
     * Search photos based on a query string.
     */
    fun searchPhotos(queryString: String?) {
        queryLiveData.postValue(queryString)
    }

    /**
     * Get the last query value.
     */
    fun lastQueryValue(): String? = queryLiveData.value
}
