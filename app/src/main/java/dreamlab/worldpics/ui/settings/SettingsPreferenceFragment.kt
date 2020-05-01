package dreamlab.worldpics.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.codemybrainsout.ratingdialog.RatingDialog
import com.google.firebase.database.FirebaseDatabase
import dagger.android.support.AndroidSupportInjection
import dreamlab.worldpics.BuildConfig
import dreamlab.worldpics.R
import dreamlab.worldpics.WorldPics
import dreamlab.worldpics.model.Feedback
import dreamlab.worldpics.util.FileUtils
import dreamlab.worldpics.util.SharedPreferenceStorage.Companion.PREFERENCE_CLEAR_CACHE
import dreamlab.worldpics.util.SharedPreferenceStorage.Companion.PREFERENCE_PRIVACY
import dreamlab.worldpics.util.SharedPreferenceStorage.Companion.PREFERENCE_RATE_US
import dreamlab.worldpics.util.SharedPreferenceStorage.Companion.PREFERENCE_REMOVE_ADS
import dreamlab.worldpics.util.SharedPreferenceStorage.Companion.PREFERENCE_VERSION
import dreamlab.worldpics.util.SharedPreferenceStorage.Companion.PREFERENCE_VISIT_PIXABAY
import dreamlab.worldpics.util.viewModelProvider
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class SettingsPreferenceFragment : PreferenceFragmentCompat() {

    companion object{
        val SETTINGS_PREFERENCE_FRAGMENT_TAG = "SETTINGS_PREFERENCE_FRAGMENT"
    }
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SettingsViewModel

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = viewModelProvider(viewModelFactory)

        val removeAds: Preference? = findPreference(PREFERENCE_REMOVE_ADS)
        removeAds?.isVisible = !WorldPics.isPremium
        removeAds?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            //TODO
            true
        }

        val buttonRateUs: Preference? = findPreference(PREFERENCE_RATE_US)
        buttonRateUs?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            activity?.let {
                RatingDialog.Builder(activity)
                    .threshold(3f)
                    .playstoreUrl("https://play.google.com/store/apps/details?id=dreamlab.worldpics&hl=en_US")
                    .onRatingBarFormSumbit { feedback ->
                        val df = SimpleDateFormat("dd MM yyyy HH:mm:ss")
                        val today = Calendar.getInstance().time
                        val reportDate = df.format(today)

                        val database = FirebaseDatabase.getInstance()
                        val reference = database.getReference("feedback")

                        reference.child(reportDate).setValue(
                            Feedback(feedback, BuildConfig.VERSION_NAME, Build.VERSION.SDK_INT)
                        )

                        Toast.makeText(activity, "Thank you!", Toast.LENGTH_SHORT).show()
                    }.build().show()
                true
            }
            false
        }

        val version: Preference? = findPreference(PREFERENCE_VERSION)
        version?.summary = BuildConfig.VERSION_NAME

        val cache: Preference? = findPreference(PREFERENCE_CLEAR_CACHE)

        activity?.let {
            val startCacheSize = FileUtils.getCacheSize(it)
            cache?.summary = String.format("Cache size: %s", startCacheSize)
        }

        cache?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            viewModel.clearCache(activity)
            activity?.let {
                val startCacheSize = FileUtils.getCacheSizeInMB(requireContext())
                cache?.summary = String.format("Cache size: %d MB", startCacheSize)
                Toast.makeText(activity, "Cache cleared!", Toast.LENGTH_SHORT).show()
            }
            true
        }

        val privacyPolicy: Preference? = findPreference(PREFERENCE_PRIVACY)
        privacyPolicy?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(WorldPics.PRIVACY_POLICY_URL))
            startActivity(intent)
            true
        }

        val visitPixabay: Preference? = findPreference(PREFERENCE_VISIT_PIXABAY)
        visitPixabay?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://pixabay.com/"))
            startActivity(intent)
            true
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    fun updateCacheSummary() {
        val startCacheSize = FileUtils.getCacheSize(requireContext())
        val cache: Preference? = findPreference(PREFERENCE_CLEAR_CACHE)
        cache?.summary = String.format("Cache size: %s", startCacheSize)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            view.setBackgroundColor(
                ContextCompat.getColor(
                    it,
                    R.color.background_material_light
                )
            ) //REMIND: fix al problema tra ripple effect al click e background
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, null)
    }
}