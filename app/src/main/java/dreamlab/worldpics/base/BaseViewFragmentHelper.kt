package dreamlab.worldpics.base

import androidx.annotation.IdRes
import dreamlab.worldpics.R

/**
 * Created by corbi on 12/04/17.
 */

interface BaseViewFragmentHelper {

    val mFragmentManager: androidx.fragment.app.FragmentManager

    fun <T : BaseFragment<*>> fragmentWithTag(tag: String = FRAGMENT_TAG): T? {
        return mFragmentManager.findFragmentByTag(tag)?.let {
            @Suppress("UNCHECKED_CAST")
            it as? T
        }
    }

    fun <T> fragmentById(id: Int): T {
        @Suppress("UNCHECKED_CAST")
        return mFragmentManager.findFragmentById(id) as T
    }

    fun replacePushFragment(@IdRes idFrameContainer: Int, baseFragment: BaseFragment<*>, withTag: String = FRAGMENT_TAG, anim: Anim = Anim.PUSH, backStack: String? = null) {
        mFragmentManager.beginTransaction()
                .addToBackStack(backStack)
                .setAnimations(anim)
                .replace(idFrameContainer, baseFragment, withTag)
                .commit()
    }

    fun replacePopFragment(@IdRes idFrameContainer: Int, baseFragment: BaseFragment<*>, withTag: String = FRAGMENT_TAG, anim: Anim = Anim.FADE) {
        mFragmentManager
                .beginTransaction()
                .setAnimations(anim)
                .replace(idFrameContainer, baseFragment, withTag)
                .commit()
    }

    fun fragmentBackStackEntryCount(): Int {
        return mFragmentManager.backStackEntryCount
    }

    fun popFragment(stackName: String? = null, inclusive: Boolean = false) {
        mFragmentManager.popBackStack(stackName, if (inclusive) androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE else 0)
    }

    fun showFragment(baseDialogFragment: BaseDialogFragment<*>, withTag: String = FRAGMENT_TAG) {
        baseDialogFragment.show(mFragmentManager, withTag)
    }

    fun showFragment(fragmentManager: androidx.fragment.app.FragmentManager, baseDialogFragment: BaseDialogFragment<*>, withTag: String = FRAGMENT_TAG) {
        baseDialogFragment.show(fragmentManager, withTag)
    }
}

enum class Anim {
    STAY, FADE, PUSH, MODAL
}

fun androidx.fragment.app.FragmentTransaction.setAnimations(anim: Anim): androidx.fragment.app.FragmentTransaction {
    return when (anim) {
        Anim.FADE -> this.setCustomAnimations(android.R.anim.fade_in, R.anim.stay, R.anim.stay, android.R.anim.fade_out)
        Anim.MODAL -> this.setCustomAnimations(R.anim.slide_in_from_bottom, R.anim.stay, R.anim.stay, R.anim.slide_out_to_bottom)
        Anim.PUSH -> this.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_left, R.anim.slide_out_to_right)
        Anim.STAY -> return this
    }
}

const val FRAGMENT_TAG = "FRAGMENT_TAG"