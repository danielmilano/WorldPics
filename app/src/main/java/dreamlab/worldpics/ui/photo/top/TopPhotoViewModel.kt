package dreamlab.worldpics.ui.photo.top

import androidx.lifecycle.*
import dreamlab.worldpics.repository.PhotoRepository
import dreamlab.worldpics.model.Photo
import dreamlab.worldpics.model.PhotoRequest
import dreamlab.worldpics.repository.Listing
import javax.inject.Inject

class TopPhotoViewModel @Inject constructor(private val repository: PhotoRepository) : ViewModel() {

    private val requestLiveData = MutableLiveData<PhotoRequest?>()

    private val photosResult: LiveData<Listing<Photo>> = Transformations.map(requestLiveData) {
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

    fun searchPhotos(photoRequest: PhotoRequest? = null) {
        requestLiveData.postValue(photoRequest)
    }

    fun retry() {
        val listing = photosResult.value
        listing?.retry?.invoke()
    }
}
