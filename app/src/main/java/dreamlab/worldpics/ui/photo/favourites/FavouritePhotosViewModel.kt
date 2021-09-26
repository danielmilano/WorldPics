package dreamlab.worldpics.ui.photo.favourites

import androidx.lifecycle.*
import dreamlab.worldpics.db.PhotoDao
import dreamlab.worldpics.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavouritePhotosViewModel @Inject constructor(val photoDao: PhotoDao): ViewModel() {

    var photoList: MutableLiveData<List<Photo>> = MutableLiveData()

    init {
        viewModelScope.launch {
            photoList.postValue(photoDao.photos())
        }
    }
}

