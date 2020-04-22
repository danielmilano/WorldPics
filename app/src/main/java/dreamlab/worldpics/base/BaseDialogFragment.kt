package dreamlab.worldpics.base

import android.content.Context
import androidx.fragment.app.Fragment
import dagger.android.support.DaggerDialogFragment
import dreamlab.worldpics.R
import dreamlab.worldpics.util.FragmentListenerHelper

/**
 * A [Fragment] with support for:
 * - Logging event
 * - Presenter Lifecycle binding
 * - Dagger getAddComponent interface
 * - Loading view
 * - Alert view
 */
abstract class BaseDialogFragment<L> : DaggerDialogFragment, BaseViewFragmentHelper,
    BaseViewAlertHelper {

    protected var mListenerHelper: FragmentListenerHelper<L>

    constructor() {
        mListenerHelper = FragmentListenerHelper(null)
    }

    constructor(listenerClass: Class<L>) {
        mListenerHelper = FragmentListenerHelper(listenerClass)
    }

    override fun getBaseViewContext() = requireContext()

    override val mFragmentManager: androidx.fragment.app.FragmentManager
        get() = childFragmentManager


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListenerHelper.onAttach(context)
    }

    override fun onDetach() {
        mListenerHelper.onDetach()
        super.onDetach()
    }

}
