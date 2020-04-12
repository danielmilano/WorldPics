package dreamlab.worldpics.base

import android.os.Bundle
import dreamlab.worldpics.R

/**
 * Created by corbi on 04/05/17.
 */

abstract class BaseTransitionableActivity : BaseActivity() {

    protected var animInOnCreate: Int = 0
    protected var animOutOnCreate: Int = 0
    protected var animInOnFinishing: Int = 0
    protected var animOutOnFinishing: Int = 0

    /**
     * Call this before of onCreate
     */
    protected fun setModalTransition() {
        animInOnCreate = R.anim.slide_in_from_bottom
        animOutOnCreate = R.anim.stay
        animInOnFinishing = R.anim.stay
        animOutOnFinishing = R.anim.slide_out_to_bottom
    }

    /**
     * Call this before of onCreate
     */
    protected fun setPushTransition() {
        animInOnCreate = R.anim.slide_in_from_right
        animOutOnCreate = R.anim.slide_out_to_left
        animInOnFinishing = R.anim.slide_in_from_left
        animOutOnFinishing = R.anim.slide_out_to_right
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(animInOnCreate, animOutOnCreate)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(animInOnFinishing, animOutOnFinishing)
    }
}
