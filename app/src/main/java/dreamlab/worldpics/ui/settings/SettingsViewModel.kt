package dreamlab.worldpics.ui.settings

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.bumptech.glide.Glide
import dreamlab.worldpics.AutoWallpaperWorker
import dreamlab.worldpics.AutoWallpaperWorker.Companion.AUTO_WALLPAPER_WORK_NAME
import dreamlab.worldpics.db.PhotoDao
import dreamlab.worldpics.model.Photo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SettingsViewModel @Inject constructor(val photoDao: PhotoDao, application: Application) : ViewModel() {

    private var mPhotos: LiveData<List<Photo>>? = null
    private val workManager = WorkManager.getInstance(application)

    init {
        viewModelScope.launch {
            mPhotos = photoDao.photos()
        }
    }

    fun setAutoWallpaper(interval: Long) {
        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(false)
            .setRequiresStorageNotLow(false)
            .build()

        val work = PeriodicWorkRequestBuilder<AutoWallpaperWorker>(interval, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(AUTO_WALLPAPER_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            work)
    }

    internal fun cancelWork() {
        workManager.cancelUniqueWork(AUTO_WALLPAPER_WORK_NAME)
    }

    fun getFavouritePhotos(): LiveData<List<Photo>>? = mPhotos

    fun clearCache(context: Context?) {
        context?.let {
            viewModelScope.launch {
                asyncAwait {
                    withContext(Dispatchers.IO) {
                        Glide.get(context).clearDiskCache()
                    }
                    withContext(Dispatchers.Main) {
                        Glide.get(context).clearMemory()
                    }
                }
            }
        }
    }

    private suspend fun <T> asyncAwait(block: suspend CoroutineScope.() -> T): T {
        return withContext(viewModelScope.coroutineContext + Dispatchers.Default) { block() }
    }
}