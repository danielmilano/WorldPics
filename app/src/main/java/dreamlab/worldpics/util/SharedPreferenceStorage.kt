package dreamlab.worldpics.util

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager

import dreamlab.worldpics.R
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Singleton
class SharedPreferenceStorage @Inject constructor(private val context: Context) {

    val prefs: Lazy<SharedPreferences> = lazy { // Lazy to prevent IO access to main thread.
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    var preferenceOrientation by StringPreference(prefs, PREFERENCE_ORIENTATION, "all")
    var preferenceCategory by StringPreference(prefs, PREFERENCE_CATEGORIES, "")
    var preferenceColor by StringPreference(prefs, PREFERENCE_COLOR, "")
    var preferenceAutowallpaper by StringPreference(prefs, PREFERENCE_WORK_MANAGER, "")

    fun resetFilterPreferences() {
        val preferencesEditor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        preferencesEditor.remove(PREFERENCE_ORIENTATION)
        preferencesEditor.remove(PREFERENCE_CATEGORIES)
        preferencesEditor.remove(PREFERENCE_COLOR)
        preferencesEditor.apply()
    }

    companion object {
        const val PREFERENCE_NAME = "worldpics"
        const val PREFERENCE_ORIENTATION = "pref_orientation"
        const val PREFERENCE_CATEGORIES = "pref_categories"
        const val PREFERENCE_COLOR = "pref_color"
        const val PREFERENCE_RATE_US = "pref_rate_us"
        const val PREFERENCE_PRIVACY = "pref_privacy"
        const val PREFERENCE_VERSION = "pref_version"
        const val PREFERENCE_VISIT_PIXABAY = "pref_visit_pixabay"
        const val PREFERENCE_CLEAR_CACHE = "pref_clear_cache"
        const val PREFERENCE_DONATE = "pref_donate"
        const val PREFERENCE_ABOUT_ME = "pref_about_me"
        const val PREFERENCE_WORK_MANAGER = "pref_work_manager"
    }
}

class BooleanPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: Boolean
) : ReadWriteProperty<Any, Boolean> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return preferences.value.getBoolean(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        preferences.value.edit { putBoolean(name, value) }
    }
}

class StringPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: String?
) : ReadWriteProperty<Any, String?> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): String? {
        return preferences.value.getString(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        preferences.value.edit { putString(name, value) }
    }
}

