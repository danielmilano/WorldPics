package dreamlab.worldpics.ui.filter

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import dreamlab.worldpics.R
import dreamlab.worldpics.base.BaseDialogFragment
import dreamlab.worldpics.databinding.FragmentFilterBinding
import dreamlab.worldpics.model.PhotoRequest
import dreamlab.worldpics.util.SharedPreferenceStorage
import javax.inject.Inject

class FilterFragment : BaseDialogFragment<FilterFragment.Listener>(Listener::class.java) {

    @Inject
    lateinit var mSharedPreferenceStorage: SharedPreferenceStorage

    private lateinit var mBinding: FragmentFilterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = FragmentFilterBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireActivity(), theme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            val fragment = activity?.supportFragmentManager?.fragmentFactory?.instantiate(
                ClassLoader.getSystemClassLoader(),
                FilterPreferenceFragment::class.java.name
            )

            childFragmentManager
                .beginTransaction()
                .add(R.id.content, fragment!!)
                .commit()
        }

        mBinding.close.setOnClickListener { dismiss() }
        mBinding.apply.setOnClickListener {
            val photoRequest = PhotoRequest.Builder()
                .orientation(mSharedPreferenceStorage.preferenceOrientation)
                .category(mSharedPreferenceStorage.preferenceCategory)
                .colors(mSharedPreferenceStorage.preferenceColor)
                .build()
            mListenerHelper.listener?.onApplyFilters(photoRequest)
            dismiss()
        }
        mBinding.reset.setOnClickListener {
            mSharedPreferenceStorage.resetPreferences()
            mListenerHelper.listener?.onResetFilters()
            dismiss()
        }
    }

    internal class FilterPreferenceFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(bundle: Bundle?, s: String?) {
            addPreferencesFromResource(R.xml.filters)
        }
    }

    interface Listener {
        fun onResetFilters()
        fun onApplyFilters(request: PhotoRequest)
    }

}