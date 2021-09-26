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

class FavouritePhotosViewModel @Inject constructor(val photoDao: PhotoDao) : ViewModel() {

    val photoList: MutableLiveData<List<Photo>> by lazy {
        MutableLiveData<List<Photo>>()
    }
    fun getFavouritePhotos(){
        viewModelScope.launch(Dispatchers.IO) {
            photoList.postValue(photoDao.photos())
        }
    }
}

