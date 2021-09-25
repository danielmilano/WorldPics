package dreamlab.worldpics.ui.photo.base

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import dreamlab.worldpics.base.BaseFragment
import dreamlab.worldpics.databinding.FragmentBasePhotosBinding
import dreamlab.worldpics.model.Photo
import dreamlab.worldpics.model.PhotoRequest
import dreamlab.worldpics.util.NetworkUtil
import java.util.*
import kotlin.math.hypot

abstract class BasePhotosFragment : BaseFragment<BasePhotosFragment.Listener>(
    Listener::class.java
) {

    abstract val viewModel: BasePhotoViewModel

    private var _mBinding: FragmentBasePhotosBinding? = null
    private val mBinding get() = _mBinding!!
    private lateinit var mAdapter: PhotoAdapter
    private lateinit var currentRequest: PhotoRequest

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _mBinding = FragmentBasePhotosBinding.inflate(inflater, container, false)
        initAdapter(viewModel, mBinding)
        currentRequest = PhotoRequest.Builder().build()
        viewModel.searchPhotos(currentRequest)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.mainToolbar.filters.setOnClickListener { mListenerHelper.listener?.onShowFiltersClick() }
        mBinding.mainToolbar.search.setOnClickListener { showToolbarSearching() }
        mBinding.searchToolbar.back.setOnClickListener { hideToolbarSearching() }
        mBinding.searchToolbar.clear.setOnClickListener { clearSearchingText() }
        mBinding.searchToolbar.editText.addTextChangedListener(object : TextWatcher {
            private var timer: Timer = Timer()
            private val DELAY: Long = 600

            override fun afterTextChanged(s: Editable?) {
                timer.cancel()
                timer = Timer()
                timer.schedule(
                    object : TimerTask() {
                        override fun run() {
                            activity?.runOnUiThread {
                                if (!TextUtils.isEmpty(s?.trim())) {
                                    viewModel.searchPhotos(currentRequest.apply {
                                        q = s.toString()
                                    })
                                }
                            }
                        }
                    },
                    DELAY
                )
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!TextUtils.isEmpty(s)) {
                    mBinding.searchToolbar.clear.visibility = View.VISIBLE
                } else {
                    mBinding.searchToolbar.clear.visibility = View.INVISIBLE
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _mBinding = null
    }

    private fun initAdapter(viewModel: BasePhotoViewModel, mBinding: FragmentBasePhotosBinding) {
        mAdapter = PhotoAdapter(::onPhotoClicked) { viewModel.retry() }
        mBinding.recycler.adapter = mAdapter
        viewModel.photos.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                if (!NetworkUtil.networkAvailable(requireContext())) {
                    mBinding.emptyPlaceholder.visibility = View.GONE
                    mBinding.noNetworkConnectionPlaceholder.visibility = View.VISIBLE
                } else {
                    mBinding.noNetworkConnectionPlaceholder.visibility = View.GONE
                    mBinding.emptyPlaceholder.visibility = View.VISIBLE
                }
            } else {
                mBinding.emptyPlaceholder.visibility = View.GONE
                mBinding.noNetworkConnectionPlaceholder.visibility = View.GONE
            }
            mAdapter.submitList(it)
        })
        viewModel.networkState.observe(viewLifecycleOwner, Observer {
            mAdapter.setNetworkState(it)
        })
    }

    private fun showToolbarSearching() {
        val viewToAppear = mBinding.searchToolbar.root
        val showSearchKeyboard = {
            mBinding.searchToolbar.editText.requestFocus()
            val imm =
                viewToAppear.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(mBinding.searchToolbar.editText, InputMethodManager.SHOW_IMPLICIT)
        }

        val cx = viewToAppear.width / 2
        val cy = viewToAppear.height / 2
        val finalRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()
        val animator =
            ViewAnimationUtils.createCircularReveal(viewToAppear, cx, cy, 0f, finalRadius)
        viewToAppear.visibility = View.VISIBLE
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                showSearchKeyboard()
            }
        })
        animator.start()

    }

    private fun hideToolbarSearching() {
        val viewToDisappear = mBinding.searchToolbar.root
        val hideSearchKeyboard = {
            mBinding.searchToolbar.editText.text = null
            mBinding.searchToolbar.editText.clearFocus()
            val imm =
                viewToDisappear.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }

        val cx = viewToDisappear.width / 2
        val cy = viewToDisappear.height / 2
        val initialRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()
        val anim =
            ViewAnimationUtils.createCircularReveal(viewToDisappear, cx, cy, initialRadius, 0f)
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                hideSearchKeyboard()
                viewToDisappear.visibility = View.INVISIBLE
            }
        })
        anim.start()
    }

    private fun clearSearchingText() {
        mBinding.searchToolbar.editText.text.clear()
    }

    private fun onPhotoClicked(photo: Photo) {
        mListenerHelper.listener?.onPhotoClicked(photo)
    }

    fun onResetFilters() {
        currentRequest = PhotoRequest.Builder().build()
        mAdapter.submitList(null)
        viewModel.searchPhotos(currentRequest)
    }

    fun onApplyFilters(request: PhotoRequest) {
        currentRequest = request.apply { q = currentRequest.q }
        mAdapter.submitList(null)
        viewModel.searchPhotos(currentRequest)
    }

    interface Listener {
        fun onShowFiltersClick()
        fun onPhotoClicked(photo : Photo)
    }

}
