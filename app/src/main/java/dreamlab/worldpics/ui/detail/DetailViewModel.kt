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
import dreamlab.worldpics.util.intentEditPhoto
import dreamlab.worldpics.util.intentSetPhotoAs
import dreamlab.worldpics.util.intentSharePhoto
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

    private suspend fun downloadPhotoAsync(context: Context, url: String): Deferred<Uri?> {
        return async {
            FileUtils.downloadPhoto(context, url)
        }
    }

    private suspend fun setPhotoAsAsync(
        context: Context,
        url: String
    ): Deferred<Pair<Intent?, Uri?>> {
        return async {
            FileUtils.setPhotoAs(context, url)
        }
    }

    fun downloadPhoto(context: Context, url: String) {
        isInProgress.postValue(true)
        launchDataLoad {
            val uri = downloadPhotoAsync(context, url)
            uri.await()?.let {
                downloadedFileUri.postValue(it)
                isInProgress.postValue(false)
            } ?: run {
                isError.postValue(true)
            }
        }
    }

    fun setPhotoAs(context: Context, url: String) {
        isInProgress.postValue(true)
        launchDataLoad {
            val pair = setPhotoAsAsync(context, url)
            pair.await().first?.let {
                downloadedFileUri.postValue(pair.await().second)
                withContext(Dispatchers.Main) {
                    context.startActivity(
                        Intent.createChooser(
                            it,
                            context.resources.getString(R.string.set_photo_as)
                        )
                    )
                }
            } ?: isError.postValue(true)
        }
    }

    fun setPhotoAs(context: Context, uri: Uri) {
        val intent = intentSetPhotoAs(uri)
        context.startActivity(
            Intent.createChooser(
                intent,
                context.resources.getString(R.string.set_photo_as)
            )
        )
    }

    fun editPhoto(context: Context, url: String) {
        isInProgress.postValue(true)
        launchDataLoad {
            downloadPhotoAsync(context, url).await()?.let {
                downloadedFileUri.postValue(it)
                withContext(Dispatchers.Main) {
                    editPhoto(context, it)
                }
            } ?: run {
                isError.postValue(true)
            }
        }
    }

    fun editPhoto(context: Context, uri: Uri) {
        val intent = intentEditPhoto(uri)
        context.startActivity(
            Intent.createChooser(
                intent,
                context.resources.getString(R.string.edit_photo)
            )
        )
    }

    fun share(context: Context, url: String) {
        isInProgress.postValue(true)
        launchDataLoad {
            downloadPhotoAsync(context, url).await()?.let {
                downloadedFileUri.postValue(it)
                withContext(Dispatchers.Main) {
                    share(context, it)
                }
            } ?: run {
                isError.postValue(true)
            }
        }
    }

    fun share(context: Context, uri: Uri) {
        val intent = intentSharePhoto(uri)
        context.startActivity(
            Intent.createChooser(
                intent,
                context.resources.getString(R.string.share_photo)
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