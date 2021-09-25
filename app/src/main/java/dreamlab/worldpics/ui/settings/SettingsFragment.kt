package dreamlab.worldpics.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dreamlab.worldpics.R
import dreamlab.worldpics.base.BaseFragment
import dreamlab.worldpics.databinding.FragmentSettingsBinding
import dreamlab.worldpics.ui.settings.SettingsPreferenceFragment.Companion.SETTINGS_PREFERENCE_FRAGMENT_TAG

class SettingsFragment : BaseFragment<Void>() {

    private var _mBinding: FragmentSettingsBinding? = null
    private val mBinding get() = _mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _mBinding = FragmentSettingsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _mBinding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            val fragment = activity?.supportFragmentManager?.fragmentFactory?.instantiate(
                ClassLoader.getSystemClassLoader(),
                SettingsPreferenceFragment::class.java.name
            )

            childFragmentManager
                .beginTransaction()
                .add(R.id.content, fragment!!, SETTINGS_PREFERENCE_FRAGMENT_TAG)
                .commit()
        }
    }

    fun updateCacheSummary() {
        (childFragmentManager.findFragmentByTag(SETTINGS_PREFERENCE_FRAGMENT_TAG) as SettingsPreferenceFragment).updateCacheSummary()
    }
}