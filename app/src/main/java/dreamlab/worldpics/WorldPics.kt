package dreamlab.worldpics

/**
 * Created by danielm on 10/02/2018.
 */

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import com.bumptech.glide.Glide
import com.facebook.stetho.Stetho
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dreamlab.worldpics.di.AppInjector
import dreamlab.worldpics.util.CrashReportingTree
import timber.log.Timber
import javax.inject.Inject

class WorldPics : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

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

        AppInjector.init(this)

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Timber.tag(TAG).w(task.exception, "getInstanceId failed")
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                token?.let {
                    Timber.d(TAG, it)
                }

            })
    }

    override fun activityInjector() = dispatchingAndroidInjector

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
