package dreamlab.worldpics.ui.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dreamlab.worldpics.R
import dreamlab.worldpics.db.PhotoDao
import dreamlab.worldpics.model.Photo
import dreamlab.worldpics.util.FileUtils
import dreamlab.worldpics.util.intentSetImageAs
import dreamlab.worldpics.util.intentShareImage
import kotlinx.coroutines.*
import javax.inject.Inject

class DetailViewModel @Inject constructor(val photoDao: PhotoDao) : ViewModel() {
    val isInProgress: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isError: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val downloadedFileUri: MutableLiveData<Uri> by lazy {
        MutableLiveData<Uri>()
    }

    suspend fun getPhotoByIdAsync(id: String): Deferred<Photo?> {
        return async {
            photoDao.getPhotoById(id)
        }
    }

    fun downloadFile(context: Context, url: String) {
        launchDataLoad {
            isInProgress.postValue(true)
            val uri = FileUtils.saveImageInGallery(context, url)
            uri?.let {
                downloadedFileUri.postValue(it)
                isInProgress.postValue(false)
            } ?: run {
                isError.postValue(true)
            }
        }
    }

    fun setAsWallpaper(context: Context, url: String) {
        launchDataLoad {
            isInProgress.postValue(true)
            val pair = FileUtils.setAsWallpaper(context, url)
            pair.first?.let {
                downloadedFileUri.postValue(pair.second)
                context.startActivity(
                    Intent.createChooser(
                        it,
                        context.resources.getString(R.string.set_photo_as)
                    )
                )
            } ?: isError.postValue(true)
        }
    }

    suspend fun setAsWallpaperAsync(context: Context, url: String): Deferred<Uri?> {
        return async {
            FileUtils.setAsWallpaper(context, url).second
        }
    }

    fun setAsWallpaper(context: Context, uri: Uri) {
        launchDataLoad {
            val intent = intentSetImageAs(uri)
            intent?.let {
                context.startActivity(
                    Intent.createChooser(
                        it,
                        context.resources.getString(R.string.set_photo_as)
                    )
                )
            } ?: run {
                isError.postValue(true)
            }
        }
    }

    fun share(context: Context, url: String) {
        launchDataLoad {
            isInProgress.postValue(true)
            setAsWallpaperAsync(context, url).await()?.let {
                withContext(Dispatchers.Main) {
                    downloadedFileUri.postValue(it)
                    share(context, it)
                }
            } ?: run {
                isError.postValue(true)
            }
        }
    }

    fun share(context: Context, uri: Uri) {
        val intent = intentShareImage(uri)
        context.startActivity(
            Intent.createChooser(
                intent,
                context.resources.getString(R.string.share_image)
            )
        )
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

    suspend fun <T> async(block: suspend CoroutineScope.() -> T): Deferred<T> {
        return viewModelScope.async(Dispatchers.Default) { block() }
    }

    suspend fun <T> asyncAwait(block: suspend CoroutineScope.() -> T): T {
        return viewModelScope.async(Dispatchers.Default) { block() }.await()
    }
}