package dreamlab.worldpics.ui.detail

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dreamlab.worldpics.R
import dreamlab.worldpics.util.FileUtils
import kotlinx.coroutines.*
import javax.inject.Inject

class DetailViewModel @Inject constructor() : ViewModel() {
    val TAG: String = DetailViewModel::class.java.simpleName

    val progressDialog: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val errorMessage: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun downloadFile(context: Context, url: String?) {
        url?.let {
            launchDataLoad {
                val success = FileUtils.saveImageInGallery(context, url)
                delay(1000)
                progressDialog.postValue(false)
                if (!success) {
                    errorMessage.postValue(true)
                }
            }
        } ?: errorMessage.postValue(true)
    }

    fun setAsWallpaper(context: Context, url: String?) {
        url?.let {
            launchDataLoad {
                val intent: Intent? = FileUtils.setAsWallpaper(context, url)
                delay(1000)
                progressDialog.postValue(false)
                if (intent != null) {
                    context.startActivity(
                        Intent.createChooser(
                            intent,
                            context.resources.getString(R.string.set_photo_as)
                        )
                    )
                } else {
                    errorMessage.postValue(true)
                }
            }
        } ?: errorMessage.postValue(true)
    }

    fun share(context: Context, url: String?) {
        url?.let {
            launchDataLoad {
                val intent: Intent? = FileUtils.shareImage(context, url)
                delay(1000)
                progressDialog.postValue(false)

                if (intent != null) {
                    context.startActivity(
                        Intent.createChooser(
                            intent,
                            context.resources.getString(R.string.share_image)
                        )
                    )
                } else {
                    errorMessage.postValue(true)
                }
            }
        } ?: errorMessage.postValue(true)
    }

    private fun launchDataLoad(block: suspend () -> Unit): Job {
        progressDialog.postValue(true)
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