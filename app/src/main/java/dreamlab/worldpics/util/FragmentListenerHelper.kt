package dreamlab.worldpics.util

import android.content.Context

/**
 * Create this instance before onAttach passing the class of listener
 * Remember to onPermissionGranted the lyfecycle methods
 * [FragmentListenerHelper.onAttach]
 * and [FragmentListenerHelper.onDetach]
 */
class FragmentListenerHelper<L>(protected val mListenerClass: Class<L>?) {

    var listener: L? = null

    fun onAttach(context: Context) {
        if (mListenerClass != null) {
            if (mListenerClass.isAssignableFrom(context.javaClass)) {
                listener = mListenerClass.cast(context)
            } else {
                throw RuntimeException(context.toString() + " must implement Listener" + mListenerClass.canonicalName)
            }
        }
    }

    fun onDetach() {
        listener = null
    }

}
