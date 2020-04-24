package dreamlab.worldpics.ui.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dreamlab.worldpics.R
import dreamlab.worldpics.util.FileUtils
import kotlinx.coroutines.*
import javax.inject.Inject

class DetailViewModel @Inject constructor() : ViewModel() {
    val isInProgress: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isError: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val downloadedFileUri: MutableLiveData<Uri> by lazy {
        MutableLiveData<Uri>()
    }

    fun downloadFile(context: Context, url: String) {
        launchDataLoad {
            isInProgress.postValue(true)
            val pair = FileUtils.saveImageInGallery(context, url)
            if (pair.first) {
                downloadedFileUri.postValue(pair.second)
                isInProgress.postValue(false)
            } else {
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

    fun setAsWallpaper(context: Context, uri: Uri) {
        launchDataLoad {
            val intent = FileUtils.setAsWallpaper(uri)
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
            val pair = FileUtils.shareImage(context, url)
            pair.first?.let {
                downloadedFileUri.postValue(pair.second)
                context.startActivity(
                    Intent.createChooser(
                        it,
                        context.resources.getString(R.string.share_image)
                    )
                )
            } ?: run {
                isError.postValue(true)
            }
        }
    }

    fun share(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/jpeg"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
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