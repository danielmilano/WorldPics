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

    private val prefs: Lazy<SharedPreferences> = lazy { // Lazy to prevent IO access to main thread.
        context.applicationContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
            .apply { changeListener }
    }

    private val observableRemoveAdsResult = MutableLiveData<Boolean>()

    private val changeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            PREFERENCE_REMOVE_ADS -> observableRemoveAdsResult.value = preferenceRemoveAds
        }
    }

    var isSettingsEnabled by BooleanPreference(prefs, PREFERENCE_ENABLE_SETTINGS, false)

    var preferenceOrientation by StringPreference(prefs, PREFERENCE_ORIENTATION, "all")

    var preferenceCategory by StringPreference(prefs, PREFERENCE_CATEGORIES, "")

    var preferenceColor by StringPreference(prefs, PREFERENCE_COLOR, "")

    var preferenceRemoveAds by BooleanPreference(prefs, PREFERENCE_REMOVE_ADS, false)

    fun resetPreference() {
        //Get this application SharedPreferences editor
        val preferencesEditor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        //Clear all the saved preference values.
        preferencesEditor.clear()
        //Read the default values and set them as the current values.
        PreferenceManager.setDefaultValues(context, R.xml.preferences, true)
        //Commit all changes.
        preferencesEditor.apply()
    }

    companion object {
        const val PREFERENCE_NAME = "worldpics"
        const val PREFERENCE_ENABLE_SETTINGS = "pref_enable_settings"
        const val PREFERENCE_ORIENTATION = "pref_orientation"
        const val PREFERENCE_CATEGORIES = "pref_categories"
        const val PREFERENCE_COLOR = "pref_color"
        const val PREFERENCE_RESET_SETTINGS = "pref_reset"
        const val PREFERENCE_REMOVE_ADS = "pref_remove_ads"
        const val PREFERENCE_RATE_US = "pref_rate_us"
        const val PREFERENCE_PRIVACY = "pref_privacy"
        const val PREFERENCE_VERSION = "pref_version"
        const val PREFERENCE_VISIT_PIXABAY = "pref_visit_pixabay"
        const val PREFERENCE_CLEAR_CACHE = "pref_clear_cache"
    }

    fun registerOnPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.value.registerOnSharedPreferenceChangeListener(listener)
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

