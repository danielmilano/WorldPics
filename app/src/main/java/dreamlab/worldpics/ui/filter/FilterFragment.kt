package dreamlab.worldpics.ui.filter

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import dreamlab.worldpics.R
import dreamlab.worldpics.databinding.FragmentFilterBinding


class FilterFragment : AppCompatDialogFragment() {

    private lateinit var mBinding: FragmentFilterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = FragmentFilterBinding.inflate(inflater, container, false)

        dialog!!.requestWindowFeature(DialogFragment.STYLE_NO_TITLE)
        isCancelable = false
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
                PreferenceFragment::class.java.name
            )

            childFragmentManager
                .beginTransaction()
                .add(R.id.content, fragment!!)
                .commit()
        }

        mBinding.close.setOnClickListener {
            dismiss()
        }
        mBinding.apply.setOnClickListener {
            dismiss()
        }
    }

    class PreferenceFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(bundle: Bundle?, s: String?) {
            addPreferencesFromResource(R.xml.filters)
        }
    }
}