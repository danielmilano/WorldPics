package dreamlab.worldpics.ui.detail

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dreamlab.worldpics.db.PhotoDao
import dreamlab.worldpics.util.FileUtils
import kotlinx.coroutines.*
import javax.inject.Inject

class DetailViewModel @Inject constructor(val photoDao: PhotoDao) : ViewModel() {
    val detailEvent: MutableLiveData<PhotoDetailEvent> by lazy {
        MutableLiveData<PhotoDetailEvent>()
    }

    fun getPhotoById(id: String) {
        viewModelScope.launch {
            try {
                val result = photoDao.getPhotoById(id)
                result?.let {
                    detailEvent.postValue(PhotoDetailEvent.OnPhotoDetailAlreadyFavourite(it))
                }
            } catch (e: Exception) {
                Log.i("DetailViewModel", "Photo not favourite!")
            }
        }
    }

    private suspend fun downloadPhotoAsync(context: Context, url: String): Deferred<Uri?> {
        return async {
            FileUtils.downloadPhoto(context, url)
        }
    }

    fun downloadPhoto(context: Context, url: String) {
        detailEvent.postValue(PhotoDetailEvent.Loading)
        launchDataLoad {
            val uri = downloadPhotoAsync(context, url)
            uri.await()?.let {
                detailEvent.postValue(PhotoDetailEvent.Completed)
                detailEvent.postValue(PhotoDetailEvent.Download(it))
            } ?: run {
                detailEvent.postValue(PhotoDetailEvent.Error)
            }
        }
    }

    fun setPhotoAs(context: Context, url: String) {
        detailEvent.postValue(PhotoDetailEvent.Loading)
        launchDataLoad {
            val uri = downloadPhotoAsync(context, url)
            uri.await()?.let {
                detailEvent.postValue(PhotoDetailEvent.Completed)
                detailEvent.postValue(PhotoDetailEvent.SetPhotoDetail(it))
            } ?: detailEvent.postValue(PhotoDetailEvent.Error)
        }
    }

    fun editPhoto(context: Context, url: String) {
        detailEvent.postValue(PhotoDetailEvent.Loading)
        launchDataLoad {
            downloadPhotoAsync(context, url).await()?.let {
                detailEvent.postValue(PhotoDetailEvent.Completed)
                detailEvent.postValue(PhotoDetailEvent.Edit(it))
            } ?: run {
                detailEvent.postValue(PhotoDetailEvent.Error)
            }
        }
    }

    fun share(context: Context, url: String) {
        detailEvent.postValue(PhotoDetailEvent.Loading)
        launchDataLoad {
            downloadPhotoAsync(context, url).await()?.let {
                detailEvent.postValue(PhotoDetailEvent.Completed)
                detailEvent.postValue(PhotoDetailEvent.Share(it))
            } ?: run {
                detailEvent.postValue(PhotoDetailEvent.Error)
            }
        }
    }

    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            asyncAwait {
                withContext(Dispatchers.IO) {
                    block()
                }
            }
        }
    }

    private suspend fun <T> async(block: suspend CoroutineScope.() -> T): Deferred<T> {
        return viewModelScope.async(Dispatchers.Default) { block() }
    }

    private suspend fun <T> asyncAwait(block: suspend CoroutineScope.() -> T): T {
        return withContext(viewModelScope.coroutineContext + Dispatchers.Default) { block() }
    }
}