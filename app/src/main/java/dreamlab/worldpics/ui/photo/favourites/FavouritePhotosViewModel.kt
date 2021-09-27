package dreamlab.worldpics.ui.photo.favourites

import androidx.lifecycle.*
import dreamlab.worldpics.db.PhotoDao
import dreamlab.worldpics.model.Photo
import dreamlab.worldpics.ui.detail.PhotoDetailEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavouritePhotosViewModel @Inject constructor(val photoDao: PhotoDao): ViewModel() {

    private var mPhotos: LiveData<List<Photo>>? = null

    init {
        viewModelScope.launch {
            mPhotos = photoDao.photos()
        }
    }

    fun getFavouritePhotos(): LiveData<List<Photo>>? = mPhotos

}

