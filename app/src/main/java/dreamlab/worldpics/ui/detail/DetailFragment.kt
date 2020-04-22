package dreamlab.worldpics.ui.detail

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import dreamlab.worldpics.base.BaseFragment
import dreamlab.worldpics.databinding.FragmentDetailBinding
import dreamlab.worldpics.model.Photo
import dreamlab.worldpics.util.FileUtils
import dreamlab.worldpics.util.PermissionUtils
import dreamlab.worldpics.util.viewModelProvider
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