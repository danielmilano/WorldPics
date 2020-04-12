package dreamlab.worldpics.base

import android.content.Context
import androidx.fragment.app.Fragment
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
abstract class BaseDialogFragment<L> : androidx.fragment.app.DialogFragment, BaseViewFragmentHelper, BaseViewAlertHelper {

    protected var mListenerHelper: FragmentListenerHelper<L>

    constructor() {
        setStyle(STYLE_NORMAL, R.style.ModalFragment)
        mListenerHelper = FragmentListenerHelper(null)
    }

    constructor(listenerClass: Class<L>) {
        setStyle(STYLE_NORMAL, R.style.ModalFragment)
        mListenerHelper = FragmentListenerHelper(listenerClass)
    }

    override fun getBaseViewContext() = context!!

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
