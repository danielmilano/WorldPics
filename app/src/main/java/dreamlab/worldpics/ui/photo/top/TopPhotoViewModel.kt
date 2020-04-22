package dreamlab.worldpics.ui.photo.top

import dreamlab.worldpics.model.PhotoRequest
import dreamlab.worldpics.repository.PhotoRepository
import dreamlab.worldpics.ui.photo.base.BasePhotoViewModel
import javax.inject.Inject

class TopPhotoViewModel @Inject constructor(val repository: PhotoRepository) :
    BasePhotoViewModel(repository) {

    override fun searchPhotos(photoRequest: PhotoRequest) {
        requestLiveData.postValue(photoRequest.apply { top = true })
    }
}