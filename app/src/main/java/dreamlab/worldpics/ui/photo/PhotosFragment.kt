package dreamlab.worldpics.ui.photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.DaggerFragment
import dreamlab.worldpics.databinding.FragmentPhotosBinding
import dreamlab.worldpics.fragment.main.photo.data.Photo
import dreamlab.worldpics.util.viewModelProvider
import javax.inject.Inject

class PhotosFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: PhotoViewModel

    private lateinit var mBinding: FragmentPhotosBinding
    private lateinit var mAdapter: PhotoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        viewModel = viewModelProvider(viewModelFactory)

        mAdapter = PhotoAdapter(::onPhotoClicked)
        mBinding = FragmentPhotosBinding.inflate(inflater, container, false)
        mBinding.recycler.adapter = mAdapter

        subscribeUi(mBinding)

        return mBinding.root
    }

    fun onPhotoClicked(photo: Photo) {
        //TODO
    }

    fun removeBannerAds() {
        //TODO
    }

    private fun subscribeUi(binding: FragmentPhotosBinding) {
        viewModel.photos.observe(viewLifecycleOwner, Observer{
            mAdapter.submitList(it)
        })
    }
}
