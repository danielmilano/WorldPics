package dreamlab.worldpics

import android.app.WallpaperManager
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import dreamlab.worldpics.db.PhotoDao
import java.lang.Exception
import java.net.URL
import javax.inject.Inject
import kotlin.random.Random


class AutoWallpaperWorker(val context: Context, params: WorkerParameters) :
    Worker(context, params) {

    @Inject
    lateinit var photoDao: PhotoDao

    companion object {
        const val AUTO_WALLPAPER_WORK_NAME = "auto_wallpaper_work"
    }

    override fun doWork(): Result {
        val list = photoDao.getAll()
        val randomIndex = Random.nextInt(list.size)
        val url = list[randomIndex].fullHDURL
        return try {
            if (!url.isNullOrEmpty()) {
                setWallpaperFromUrl(url)
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun setWallpaperFromUrl(url: String) {
        val wpm = WallpaperManager.getInstance(context)
        URL(url).openStream().use {
            wpm.setStream(it)
        }
    }
}