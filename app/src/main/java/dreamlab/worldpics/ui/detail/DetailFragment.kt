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
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.transition.AutoTransition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import dreamlab.worldpics.R
import dreamlab.worldpics.base.BaseFragment
import dreamlab.worldpics.databinding.FragmentDetailBinding
import dreamlab.worldpics.db.PhotoDao
import dreamlab.worldpics.model.Photo
import dreamlab.worldpics.util.PermissionUtils
import dreamlab.worldpics.util.viewModelProvider
import kotlinx.android.synthetic.main.fab_menu.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DetailFragment : BaseFragment<DetailFragment.Listener>(Listener::class.java) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var viewModel: DetailViewModel? = null

    @Inject
    lateinit var photoDao: PhotoDao

    private var _mBinding: FragmentDetailBinding? = null
    private val mBinding get() = _mBinding!!
    private var downloadedFileUri: Uri? = null

    private val CAN_REMOVE_FAVOURITE = 0
    private val CAN_ADD_FAVOURITE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _mBinding = FragmentDetailBinding.inflate(inflater, container, false)
        mBinding.photo = requireArguments().getSerializable(ARG_PHOTO) as Photo
        viewModel = viewModelProvider(viewModelFactory)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel?.isInProgress?.observe(viewLifecycleOwner, Observer {
            if (it) {
                Toast.makeText(requireContext(), "Downloading image...", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Download completed!", Toast.LENGTH_SHORT).show()
            }
        })
        viewModel?.isError?.observe(viewLifecycleOwner,
            Observer {
                if (it) {
                    Toast.makeText(
                        requireContext(),
                        "Error while downloading the image. Please try again later.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
        viewModel?.downloadedFileUri?.observe(
            viewLifecycleOwner,
            Observer {
                downloadedFileUri = it
            }
        )

        mBinding.detailToolbar.back.setOnClickListener { mListenerHelper.listener!!.onBackPressed() }
        mBinding.detailToolbar.website.setOnClickListener {
            val userProfileUrl =
                "https://pixabay.com/en/users/${mBinding.photo!!.user}-${mBinding.photo!!.user_id}/"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(userProfileUrl))
            startActivity(intent)
        }
        mBinding.fabMenu.fab.setOnClickListener { toggleFab() }
        mBinding.detailToolbar.share.setOnClickListener {
            if (hasWriteExternalStoragePermission(PermissionUtils.RequestCodeType.SHARE_PHOTO_REQUEST_CODE)) {
                sharePhoto()
            }
        }
        mBinding.fabMenu.fabItemDownloadWallpaper.setOnClickListener {
            if (hasWriteExternalStoragePermission(PermissionUtils.RequestCodeType.DOWNLOAD_PHOTO_REQUEST_CODE)) {
                downloadPhoto()
            }
        }
        mBinding.fabMenu.fabItemSetWallpaper.setOnClickListener {
            if (hasWriteExternalStoragePermission(PermissionUtils.RequestCodeType.SET_PHOTO_AS_REQUEST_CODE)) {
                setPhotoAs()
            }
        }

        viewModel?.viewModelScope?.launch {
            viewModel?.getPhotoByIdAsync(mBinding.photo!!.id)?.await()?.let {
                withContext(Dispatchers.Main) {
                    mBinding.fabMenu.fabItemAddFavourite.tag = CAN_REMOVE_FAVOURITE
                    mBinding.fabMenu.fabItemAddFavourite.text.text =
                        requireContext().getString(R.string.remove_from_favourites)
                    mBinding.fabMenu.fabItemAddFavourite.icon.setImageDrawable(
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_border_white)
                    )
                }
            } ?: kotlin.run {
                withContext(Dispatchers.Main) {
                    mBinding.fabMenu.fabItemAddFavourite.tag = CAN_ADD_FAVOURITE
                    mBinding.fabMenu.fabItemAddFavourite.text.text =
                        requireContext().getString(R.string.add_to_favourites)
                    mBinding.fabMenu.fabItemAddFavourite.icon.setImageDrawable(
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_white)
                    )
                }
            }
        }

        mBinding.fabMenu.fabItemAddFavourite.setOnClickListener {
            when (mBinding.fabMenu.fabItemAddFavourite.tag) {
                CAN_REMOVE_FAVOURITE -> {
                    viewModel?.viewModelScope?.launch {
                        withContext(Dispatchers.IO) {
                            photoDao.deletePhoto(mBinding.photo!!.id)
                        }
                    }
                    mBinding.fabMenu.fabItemAddFavourite.tag = CAN_ADD_FAVOURITE
                    mBinding.fabMenu.fabItemAddFavourite.text.text =
                        requireContext().getString(R.string.add_to_favourites)
                    mBinding.fabMenu.fabItemAddFavourite.icon.setImageDrawable(
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_white)
                    )
                    Toast.makeText(
                        requireContext(),
                        "Removed from favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                CAN_ADD_FAVOURITE -> {
                    viewModel?.viewModelScope?.launch {
                        withContext(Dispatchers.IO) {
                            photoDao.insert(mBinding.photo!!)
                        }
                    }
                    mBinding.fabMenu.fabItemAddFavourite.tag = CAN_REMOVE_FAVOURITE
                    mBinding.fabMenu.fabItemAddFavourite.text.text =
                        requireContext().getString(R.string.remove_from_favourites)
                    mBinding.fabMenu.fabItemAddFavourite.icon.setImageDrawable(
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_border_white)
                    )
                    Toast.makeText(
                        requireContext(),
                        "Added to favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _mBinding = null
    }

    private fun hasWriteExternalStoragePermission(requestCode: PermissionUtils.RequestCodeType): Boolean {
        return PermissionUtils.isStoragePermissionGranted(
            requireActivity(),
            this,
            requestCode
        )
    }

    /**
     * Toggle fab
     */
    private fun toggleFab() {
        val duration = 200L
        val transitionMove = AutoTransition()
        transitionMove.duration = duration
        TransitionManager.beginDelayedTransition(mBinding.fabMenu.root, transitionMove)
        val constraintSet = ConstraintSet()
        constraintSet.clone(mBinding.fabMenu.root)
        if (mBinding.fabMenu.fab.tag == 1) {
            // close
            mBinding.fabMenu.fab.tag = null
            val rotateAnimation = RotateAnimation(
                180.0f,
                0.0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            rotateAnimation.fillAfter = true
            rotateAnimation.duration = duration
            mBinding.fabMenu.fab.startAnimation(rotateAnimation)
            constraintSet.connect(
                mBinding.fabMenu.fabItemDownloadWallpaper.id,
                ConstraintSet.BOTTOM,
                mBinding.fabMenu.fab.id,
                ConstraintSet.BOTTOM
            )
            constraintSet.connect(
                mBinding.fabMenu.fabItemSetWallpaper.id,
                ConstraintSet.BOTTOM,
                mBinding.fabMenu.fab.id,
                ConstraintSet.BOTTOM
            )
            constraintSet.connect(
                mBinding.fabMenu.fabItemAddFavourite.id,
                ConstraintSet.BOTTOM,
                mBinding.fabMenu.fab.id,
                ConstraintSet.BOTTOM
            )
            /*constraintSet.connect(
                mBinding.fabMenu.fabItemEditPhoto.id,
                ConstraintSet.BOTTOM,
                mBinding.fabMenu.fab.id,
                ConstraintSet.BOTTOM
            )*/
            constraintSet.applyTo(mBinding.fabMenu.root)
            constraintSet.setVisibility(mBinding.fabMenu.fabItemDownloadWallpaper.id, View.INVISIBLE)
            constraintSet.setVisibility(mBinding.fabMenu.fabItemSetWallpaper.id, View.INVISIBLE)
            constraintSet.setVisibility(mBinding.fabMenu.fabItemAddFavourite.id, View.INVISIBLE)
            //constraintSet.setVisibility(mBinding.fabMenu.fabItemEditPhoto.id, View.INVISIBLE)
            mBinding.fabMenu.fabItemDownloadWallpaper.animate().alpha(0f).setDuration(duration)
                .withEndAction {
                    mBinding.fabMenu.fabItemDownloadWallpaper.visibility = View.INVISIBLE
                }
                .start()
            mBinding.fabMenu.fabItemSetWallpaper.animate().alpha(0f).setDuration(duration)
                .withEndAction { mBinding.fabMenu.fabItemSetWallpaper.visibility = View.INVISIBLE }
                .start()
            mBinding.fabMenu.fabItemAddFavourite.animate().alpha(0f).setDuration(duration)
                .withEndAction { mBinding.fabMenu.fabItemAddFavourite.visibility = View.INVISIBLE }
                .start()
            /*mBinding.fabMenu.fabItemEditPhoto.animate().alpha(0f).setDuration(duration)
                .withEndAction { mBinding.fabMenu.fabItemEditPhoto.visibility = View.INVISIBLE }
                .start()*/
        } else {
            // open
            mBinding.fabMenu.fab.tag = 1
            val rotateAnimation = RotateAnimation(
                0.0f,
                180.0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            rotateAnimation.fillAfter = true
            rotateAnimation.duration = duration
            mBinding.fabMenu.fab.startAnimation(rotateAnimation)
            constraintSet.connect(
                mBinding.fabMenu.fabItemDownloadWallpaper.id,
                ConstraintSet.BOTTOM,
                mBinding.fabMenu.fab.id,
                ConstraintSet.TOP,
                requireContext().dpToPx(16f)
            )
            constraintSet.connect(
                mBinding.fabMenu.fabItemSetWallpaper.id,
                ConstraintSet.BOTTOM,
                mBinding.fabMenu.fabItemDownloadWallpaper.id,
                ConstraintSet.TOP,
                requireContext().dpToPx(16f)
            )
            constraintSet.connect(
                mBinding.fabMenu.fabItemAddFavourite.id,
                ConstraintSet.BOTTOM,
                mBinding.fabMenu.fabItemSetWallpaper.id,
                ConstraintSet.TOP,
                requireContext().dpToPx(16f)
            )
            /*constraintSet.connect(
                mBinding.fabMenu.fabItemEditPhoto.id,
                ConstraintSet.BOTTOM,
                mBinding.fabMenu.fabItemAddFavourite.id,
                ConstraintSet.TOP,
                requireContext().dpToPx(16f)
            )*/
            constraintSet.applyTo(mBinding.fabMenu.root)
            constraintSet.setVisibility(mBinding.fabMenu.fabItemDownloadWallpaper.id, View.VISIBLE)
            constraintSet.setVisibility(mBinding.fabMenu.fabItemSetWallpaper.id, View.VISIBLE)
            constraintSet.setVisibility(mBinding.fabMenu.fabItemAddFavourite.id, View.VISIBLE)
            //constraintSet.setVisibility(mBinding.fabMenu.fabItemEditPhoto.id, View.VISIBLE)
            mBinding.fabMenu.fabItemDownloadWallpaper.animate().alpha(1f).setDuration(duration / 2)
                .withEndAction {
                    mBinding.fabMenu.fabItemDownloadWallpaper.visibility = View.VISIBLE
                }.start()
            mBinding.fabMenu.fabItemSetWallpaper.animate().alpha(1f).setDuration(duration / 2)
                .withEndAction { mBinding.fabMenu.fabItemSetWallpaper.visibility = View.VISIBLE }
                .start()
            mBinding.fabMenu.fabItemAddFavourite.animate().alpha(1f).setDuration(duration / 2)
                .withEndAction { mBinding.fabMenu.fabItemAddFavourite.visibility = View.VISIBLE }
                .start()
           /* mBinding.fabMenu.fabItemEditPhoto.animate().alpha(1f).setDuration(duration / 2)
                .withEndAction { mBinding.fabMenu.fabItemEditPhoto.visibility = View.VISIBLE }
                .start()*/
        }
    }

    private fun Context.dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            this.resources.displayMetrics
        ).toInt()
    }

    private fun editPhoto() {
        downloadedFileUri?.let {
            viewModel?.editPhoto(requireContext(), it)
        } ?: run {
            viewModel?.editPhoto(requireContext(), mBinding.photo!!.fullHDURL!!)
        }
    }

    private fun sharePhoto() {
        downloadedFileUri?.let {
            viewModel?.share(requireContext(), it)
        } ?: run {
            viewModel?.share(requireContext(), mBinding.photo!!.fullHDURL!!)
        }
    }

    private fun downloadPhoto() {
        downloadedFileUri?.let {
            Toast.makeText(context, "Image already downloaded!", Toast.LENGTH_SHORT).show()
        } ?: run {
            viewModel?.downloadPhoto(requireContext(), mBinding.photo!!.fullHDURL!!)
        }
    }

    private fun setPhotoAs() {
        downloadedFileUri?.let {
            viewModel?.setPhotoAs(requireContext(), it)
        } ?: run {
            viewModel?.setPhotoAs(requireContext(), mBinding.photo!!.fullHDURL!!)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionUtils.RequestCodeType.SHARE_PHOTO_REQUEST_CODE.id -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sharePhoto()
                }
            }
            PermissionUtils.RequestCodeType.DOWNLOAD_PHOTO_REQUEST_CODE.id -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadPhoto()
                }
            }
            PermissionUtils.RequestCodeType.SET_PHOTO_AS_REQUEST_CODE.id -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setPhotoAs()
                }
            }
            PermissionUtils.RequestCodeType.EDIT_PHOTO_REQUEST_CODE.id -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    editPhoto()
                }
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