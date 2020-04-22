package dreamlab.worldpics.ui.photo

import androidx.lifecycle.*
import dreamlab.worldpics.repository.PhotoRepository
import dreamlab.worldpics.model.Photo
import dreamlab.worldpics.repository.Listing
import javax.inject.Inject

class PhotoViewModel @Inject constructor(private val repository: PhotoRepository) : ViewModel() {

    private val queryLiveData = MutableLiveData<String?>()

    private val photosResult: LiveData<Listing<Photo>> = Transformations.map(queryLiveData) {
        it?.let {
            repository.searchPhotos(it)
        } ?: kotlin.run {
            repository.getPhotos()
        }
    }

    val networkState = photosResult.switchMap { it.networkState }
    val photos = photosResult.switchMap {
        it.pagedList
    }

    fun searchPhotos(queryString: String? = null) {
        queryLiveData.postValue(queryString)
    }

    fun retry() {
        val listing = photosResult.value
        listing?.retry?.invoke()
    }
}
