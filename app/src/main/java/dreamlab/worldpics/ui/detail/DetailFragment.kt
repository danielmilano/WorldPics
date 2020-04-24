package dreamlab.worldpics.ui.detail

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.ViewModelProvider
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import dreamlab.worldpics.R
import dreamlab.worldpics.base.BaseFragment
import dreamlab.worldpics.databinding.FragmentDetailBinding
import dreamlab.worldpics.model.Photo
import dreamlab.worldpics.util.PermissionUtils
import dreamlab.worldpics.util.viewModelProvider
import kotlinx.android.synthetic.main.fragment_filter.*
import javax.inject.Inject


class DetailFragment : BaseFragment<DetailFragment.Listener>(Listener::class.java) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var viewModel: DetailViewModel? = null

    private lateinit var mBinding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = FragmentDetailBinding.inflate(inflater, container, false)
        mBinding.photo = requireArguments().getSerializable(ARG_PHOTO) as Photo
        viewModel = viewModelProvider(viewModelFactory)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.detailToolbar.back.setOnClickListener { mListenerHelper.listener!!.onBackPressed() }
        mBinding.detailToolbar.share.setOnClickListener {
            if (PermissionUtils.isStoragePermissionGranted(
                    requireActivity(),
                    this,
                    PermissionUtils.RequestCodeType.SHARE_REQUEST_CODE
                )
            ) {
                viewModel?.share(requireContext(), mBinding.photo!!.fullHDURL)
            }
        }
        mBinding.detailToolbar.website.setOnClickListener {
            val userProfileUrl =
                "https://pixabay.com/en/users/${mBinding.photo!!.user}-${mBinding.photo!!.user_id}/"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(userProfileUrl))
            startActivity(intent)
        }
        mBinding.fab.setOnClickListener { toggleFab() }
        mBinding.fabItemDownloadWallpaper.setOnClickListener { }
        mBinding.fabItemSetWallpaper.setOnClickListener { }
        mBinding.fabItemInfo.setOnClickListener { }
    }

    /**
     * Toggle fab
     */
    private fun toggleFab() {
        val duration = 200L
        val transitionMove = AutoTransition()
        transitionMove.duration = duration
        TransitionManager.beginDelayedTransition(mBinding.constraintLayoutOfFabs, transitionMove)
        val constraintSet = ConstraintSet()
        constraintSet.clone(mBinding.constraintLayoutOfFabs)
        if (mBinding.fab.tag == 1) {
            // close
            mBinding.fab.tag = null
            val rotateAnimation = RotateAnimation(180.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            rotateAnimation.fillAfter = true
            rotateAnimation.duration = duration
            mBinding.fab.startAnimation(rotateAnimation)
            constraintSet.connect(mBinding.fabItemDownloadWallpaper.id, ConstraintSet.BOTTOM, mBinding.fab.id, ConstraintSet.BOTTOM)
            constraintSet.connect(mBinding.fabItemSetWallpaper.id, ConstraintSet.BOTTOM, mBinding.fab.id, ConstraintSet.BOTTOM)
            constraintSet.connect(mBinding.fabItemInfo.id, ConstraintSet.BOTTOM, mBinding.fab.id, ConstraintSet.BOTTOM)
            constraintSet.applyTo(mBinding.constraintLayoutOfFabs)
            constraintSet.setVisibility(mBinding.fabItemDownloadWallpaper.id, View.INVISIBLE)
            constraintSet.setVisibility(mBinding.fabItemSetWallpaper.id, View.INVISIBLE)
            constraintSet.setVisibility(mBinding.fabItemInfo.id, View.INVISIBLE)
            mBinding.fabItemDownloadWallpaper.animate().alpha(0f).setDuration(duration).withEndAction { mBinding.fabItemDownloadWallpaper.visibility = View.INVISIBLE }.start()
            mBinding.fabItemSetWallpaper.animate().alpha(0f).setDuration(duration).withEndAction { mBinding.fabItemSetWallpaper.visibility = View.INVISIBLE }.start()
            mBinding.fabItemInfo.animate().alpha(0f).setDuration(duration).withEndAction { mBinding.fabItemInfo.visibility = View.INVISIBLE }.start()
        } else {
            // open
            mBinding.fab.tag = 1
            val rotateAnimation = RotateAnimation(0.0f, 180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            rotateAnimation.fillAfter = true
            rotateAnimation.duration = duration
            mBinding.fab.startAnimation(rotateAnimation)
            constraintSet.connect(mBinding.fabItemDownloadWallpaper.id, ConstraintSet.BOTTOM, mBinding.fab.id, ConstraintSet.TOP, requireContext().dpToPx(16f))
            constraintSet.connect(mBinding.fabItemSetWallpaper.id, ConstraintSet.BOTTOM, mBinding.fabItemDownloadWallpaper.id, ConstraintSet.TOP, requireContext().dpToPx(16f))
            constraintSet.connect(mBinding.fabItemInfo.id, ConstraintSet.BOTTOM, mBinding.fabItemSetWallpaper.id, ConstraintSet.TOP, requireContext().dpToPx(16f))
            constraintSet.applyTo(mBinding.constraintLayoutOfFabs)
            constraintSet.setVisibility(mBinding.fabItemDownloadWallpaper.id, View.VISIBLE)
            constraintSet.setVisibility(mBinding.fabItemSetWallpaper.id, View.VISIBLE)
            constraintSet.setVisibility(mBinding.fabItemInfo.id, View.VISIBLE)
            mBinding.fabItemDownloadWallpaper.animate().alpha(1f).setDuration(duration / 2).withEndAction { mBinding.fabItemDownloadWallpaper.visibility = View.VISIBLE }.start()
            mBinding.fabItemSetWallpaper.animate().alpha(1f).setDuration(duration / 2).withEndAction { mBinding.fabItemSetWallpaper.visibility = View.VISIBLE }.start()
            mBinding.fabItemInfo.animate().alpha(1f).setDuration(duration / 2).withEndAction { mBinding.fabItemInfo.visibility = View.VISIBLE }.start()
        }
    }

    private fun Context.dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            this.resources.displayMetrics
        ).toInt()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionUtils.RequestCodeType.SHARE_REQUEST_CODE.id -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel?.share(requireContext(), mBinding.photo!!.fullHDURL)
                }
            }
            PermissionUtils.RequestCodeType.DOWNLOAD_REQUEST_CODE.id -> {
                //TODO
            }
            PermissionUtils.RequestCodeType.SET_AS_WALLPAPER_REQUEST_CODE.id -> {
                //TODO
            }
        }
    }


    companion object {
        private const val ARG_PHOTO = "ARG_PHOTO"

        fun newInstance(photo: Photo): DetailFragment {
            val fragment = DetailFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARG_PHOTO, photo)
            fragment.arguments = bundle
            return fragment
        }
    }

    interface Listener {
        fun onBackPressed()
    }
}