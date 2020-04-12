/*
 * Copyright (c) 2017 by dottori.it.
 */

package dreamlab.worldpics.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import dreamlab.worldpics.util.FragmentListenerHelper

/**
 * A simple [Fragment] subclass.
 */
abstract class BaseFragment<L> : Fragment, BaseViewFragmentHelper {

    protected var mListenerHelper: FragmentListenerHelper<L>

    constructor() {
        mListenerHelper = FragmentListenerHelper(null)
    }

    constructor(listenerClass: Class<L>) {
        mListenerHelper = FragmentListenerHelper(listenerClass)
    }

    override val mFragmentManager: androidx.fragment.app.FragmentManager
        get() = childFragmentManager

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    @CallSuper
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListenerHelper.onAttach(context)
    }

    @CallSuper
    override fun onDetach() {
        mListenerHelper.onDetach()
        super.onDetach()
    }
}
