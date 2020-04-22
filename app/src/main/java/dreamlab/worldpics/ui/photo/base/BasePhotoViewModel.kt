package dreamlab.worldpics.ui.photo.base

import androidx.lifecycle.*
import dreamlab.worldpics.repository.PhotoRepository
import dreamlab.worldpics.model.Photo
import dreamlab.worldpics.model.PhotoRequest
import dreamlab.worldpics.repository.Listing

abstract class BasePhotoViewModel constructor(private val repository: PhotoRepository) :
    ViewModel() {

    protected val requestLiveData = MutableLiveData<PhotoRequest?>()

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

    abstract fun searchPhotos(photoRequest: PhotoRequest)

    fun retry() {
        val listing = photosResult.value
        listing?.retry?.invoke()
    }
}
