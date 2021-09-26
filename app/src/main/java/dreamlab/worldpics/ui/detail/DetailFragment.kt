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
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.transition.AutoTransition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dreamlab.worldpics.R
import dreamlab.worldpics.base.BaseFragment
import dreamlab.worldpics.databinding.FragmentDetailBinding
import dreamlab.worldpics.db.PhotoDao
import dreamlab.worldpics.model.Photo
import dreamlab.worldpics.util.*
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
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
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

        setClickListeners()
        setupObservers()
        viewModel?.getPhotoById(mBinding.photo!!.id)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _mBinding = null
    }

    private fun setClickListeners() {
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
        mBinding.fabMenu.fabItemAddFavourite.tag = CAN_ADD_FAVOURITE
        mBinding.fabMenu.fabItemAddFavourite.setOnClickListener {
            when (mBinding.fabMenu.fabItemAddFavourite.tag) {
                CAN_REMOVE_FAVOURITE -> {
                    viewModel?.removePhotoFromFavourites(mBinding.photo!!.id)
                    canAddToFavourites()
                    Toast.makeText(
                        requireContext(),
                        "Removed from favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                CAN_ADD_FAVOURITE -> {
                    viewModel?.addPhotoToFavourites(mBinding.photo!!)
                    canRemoveToFavourites()
                    Toast.makeText(requireContext(), "Added to favourites", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel?.detailEvent?.observe(viewLifecycleOwner) {
            when (it) {
                PhotoDetailEvent.Loading -> Toast.makeText(
                    requireContext(),
                    "Downloading image...",
                    Toast.LENGTH_SHORT
                ).show()
                PhotoDetailEvent.Error -> Toast.makeText(
                    requireContext(),
                    "Error while downloading the image. Please try again later.",
                    Toast.LENGTH_SHORT
                ).show()
                is PhotoDetailEvent.Downloaded -> {
                    downloadedFileUri = it.uri
                    Toast.makeText(requireContext(), "Download completed!", Toast.LENGTH_SHORT)
                        .show()
                }
                is PhotoDetailEvent.Edit -> {
                    Toast.makeText(requireContext(), "Download completed!", Toast.LENGTH_SHORT)
                        .show()
                    sendEditPhotoIntent(it.uri)
                }
                is PhotoDetailEvent.SetPhotoAs -> {
                    Toast.makeText(requireContext(), "Download completed!", Toast.LENGTH_SHORT)
                        .show()
                    sendSetPhotoAsIntent(it.uri)
                }
                is PhotoDetailEvent.Share -> {
                    Toast.makeText(requireContext(), "Download completed!", Toast.LENGTH_SHORT)
                        .show()
                    sendShareIntent(it.uri)
                }
                is PhotoDetailEvent.IsFavourite -> {
                    if (it.photo != null) {
                        canRemoveToFavourites()
                    } else {
                        canAddToFavourites()
                    }
                }
            }
        }
    }

    private fun canRemoveToFavourites() {
        mBinding.fabMenu.fabItemAddFavourite.tag = CAN_REMOVE_FAVOURITE
        mBinding.fabMenu.fabItemAddFavourite.findViewById<TextView>(R.id.text).text =
            requireContext().getString(R.string.remove_from_favourites)
        mBinding.fabMenu.fabItemAddFavourite.findViewById<FloatingActionButton>(R.id.icon)
            .setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_favorite_border_white
                )
            )
    }

    private fun canAddToFavourites() {
        mBinding.fabMenu.fabItemAddFavourite.tag = CAN_ADD_FAVOURITE
        mBinding.fabMenu.fabItemAddFavourite.findViewById<TextView>(R.id.text).text =
            requireContext().getString(R.string.add_to_favourites)
        mBinding.fabMenu.fabItemAddFavourite.findViewById<FloatingActionButton>(R.id.icon)
            .setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_favorite_white
                )
            )
    }

    private fun hasWriteExternalStoragePermission(requestCode: PermissionUtils.RequestCodeType): Boolean {
        return PermissionUtils.isStoragePermissionGranted(
            requireActivity(),
            this,
            requestCode
        )
    }

    private fun editPhoto() {
        downloadedFileUri?.let {
            sendEditPhotoIntent(it)
        } ?: run {
            viewModel?.editPhoto(requireContext(), mBinding.photo!!.fullHDURL!!)
        }
    }

    private fun downloadPhoto() {
        downloadedFileUri?.let {
            Toast.makeText(context, "Image already downloaded!", Toast.LENGTH_SHORT).show()
        } ?: run {
            viewModel?.downloadPhoto(requireContext(), mBinding.photo!!.fullHDURL!!)
        }
    }

    private fun sharePhoto() {
        downloadedFileUri?.let {
            sendShareIntent(it)
        } ?: run {
            viewModel?.share(requireContext(), mBinding.photo!!.fullHDURL!!)
        }
    }

    private fun setPhotoAs() {
        downloadedFileUri?.let {
            sendSetPhotoAsIntent(it)
        } ?: run {
            viewModel?.setPhotoAs(requireContext(), mBinding.photo!!.fullHDURL!!)
        }
    }

    private fun sendEditPhotoIntent(uri: Uri) {
        val intent = intentEditPhoto(uri)
        requireContext().startActivity(
            Intent.createChooser(
                intent,
                requireContext().resources.getString(R.string.edit_photo)
            )
        )
    }

    private fun sendShareIntent(uri: Uri) {
        val intent = intentSharePhoto(uri)
        requireContext().startActivity(
            Intent.createChooser(
                intent,
                requireContext().resources.getString(R.string.share_photo)
            )
        )
    }

    private fun sendSetPhotoAsIntent(uri: Uri) {
        val intent = intentSetPhotoAs(uri)
        requireContext().startActivity(
            Intent.createChooser(
                intent,
                requireContext().resources.getString(R.string.set_photo_as)
            )
        )
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
            constraintSet.setVisibility(
                mBinding.fabMenu.fabItemDownloadWallpaper.id,
                View.INVISIBLE
            )
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
}