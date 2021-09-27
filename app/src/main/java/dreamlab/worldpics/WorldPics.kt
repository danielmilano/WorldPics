package dreamlab.worldpics

/**
 * Created by danielm on 10/02/2018.
 */

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dreamlab.worldpics.di.component.DaggerApplicationComponent
import dreamlab.worldpics.util.SharedPreferenceStorage
import javax.inject.Inject

class WorldPics : DaggerApplication() {

    companion object {
        val TAG = WorldPics::class.java.simpleName

        val PRIVACY_POLICY_URL =
            "https://htmlpreview.github.io/?https://github.com/danielmilano/WorldPics/blob/master/Privacy%20Policy.html"

        val MAX_CACHE_SIZE = 30000000L
    }

    @Inject
    lateinit var mSharedPreferenceStorage: SharedPreferenceStorage

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        mSharedPreferenceStorage.resetFilterPreferences()
    }
}
