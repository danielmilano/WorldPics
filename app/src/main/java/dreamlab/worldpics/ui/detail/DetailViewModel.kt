package dreamlab.worldpics.ui.detail

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dreamlab.worldpics.db.PhotoDao
import dreamlab.worldpics.model.Photo
import dreamlab.worldpics.util.FileUtils
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class DetailViewModel @Inject constructor(val photoDao: PhotoDao) : ViewModel() {
    val detailEvent: MutableLiveData<PhotoDetailEvent> by lazy {
        MutableLiveData<PhotoDetailEvent>()
    }

    fun getPhotoById(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = photoDao.getPhotoById(id)
                detailEvent.postValue(PhotoDetailEvent.IsFavourite(result))
            } catch (e: Exception) {
                Log.i("DetailViewModel", "Photo not favourite!")
            }
        }
    }

    fun removePhotoFromFavourites(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            photoDao.deletePhoto(id)
        }
    }

    fun addPhotoToFavourites(context: Context, photo: Photo) {
        Glide.with(context)
            .asBitmap()
            .load(photo.largeImageURL)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val stream = ByteArrayOutputStream()
                    resource.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val byteArray: ByteArray = stream.toByteArray()
                    resource.recycle()
                    photo.imageBlob = byteArray
                    viewModelScope.launch(Dispatchers.IO) {
                        photoDao.insert(photo)
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
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
                detailEvent.postValue(PhotoDetailEvent.Downloaded(it))
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
                detailEvent.postValue(PhotoDetailEvent.SetPhotoAs(it))
            } ?: detailEvent.postValue(PhotoDetailEvent.Error)
        }
    }

    fun editPhoto(context: Context, url: String) {
        detailEvent.postValue(PhotoDetailEvent.Loading)
        launchDataLoad {
            downloadPhotoAsync(context, url).await()?.let {
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