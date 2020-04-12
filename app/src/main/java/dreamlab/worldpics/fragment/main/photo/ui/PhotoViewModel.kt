package dreamlab.worldpics.fragment.main.photo.ui

import androidx.lifecycle.ViewModel
import dreamlab.worldpics.fragment.main.photo.data.PhotoRepository
import javax.inject.Inject

class PhotoViewModel @Inject constructor(repository: PhotoRepository) : ViewModel() {

    val photos = repository.observePhotos()
}
