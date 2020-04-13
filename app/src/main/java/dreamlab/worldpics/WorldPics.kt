package dreamlab.worldpics

/**
 * Created by danielm on 10/02/2018.
 */

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentCallbacks2
import com.bumptech.glide.Glide
import com.facebook.stetho.Stetho
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dreamlab.worldpics.di.component.DaggerApplicationComponent
import dreamlab.worldpics.util.CrashReportingTree
import timber.log.Timber

class WorldPics : DaggerApplication() {

    var currentActivity: Activity? = null

    companion object {
        val TAG = WorldPics::class.java.simpleName

        val PRIVACY_POLICY_URL =
            "https://htmlpreview.github.io/?https://github.com/danielmilano/WorldPics/blob/master/Privacy%20Policy.html"

        var isPremium: Boolean = true

        val MAX_CACHE_SIZE = 30000000L
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) Stetho.initializeWithDefaults(this)
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        else Timber.plant(CrashReportingTree())
    }

    @SuppressLint("SwitchIntDef")
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        when (level) {
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW,
            ComponentCallbacks2.TRIM_MEMORY_COMPLETE,
            ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL,
            ComponentCallbacks2.TRIM_MEMORY_BACKGROUND -> {
                clearGlideCache()
            }
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        clearGlideCache()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.factory().create(this)
    }

    private fun clearGlideCache() {
        Thread(Runnable { Glide.get(this).clearDiskCache() }).start()

        currentActivity?.let {
            if (!it.isFinishing) {
                it.runOnUiThread {
                    Glide.get(this).clearMemory()
                }
            }
        }

        this.cacheDir.deleteRecursively()
    }
}
