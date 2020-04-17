package dreamlab.worldpics.ui.photo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ads.consent.ConsentInformation
import com.google.ads.consent.ConsentStatus
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.DaggerFragment
import dreamlab.worldpics.WorldPics
import dreamlab.worldpics.databinding.FragmentPhotosBinding
import dreamlab.worldpics.model.Photo
import dreamlab.worldpics.repository.NetworkState
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

        val builder = AdRequest.Builder()
        if (!WorldPics.isPremium) {
            val extras = Bundle()
            extras.putBoolean("is_designed_for_families", true)

            if (ConsentInformation.getInstance(context).consentStatus == ConsentStatus.NON_PERSONALIZED) {
                extras.putString("npa", "1")
                builder.addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
            }
        }

        mBinding = FragmentPhotosBinding.inflate(inflater, container, false)

        initAdapter(builder.build(), viewModel, mBinding)

        viewModel.searchPhotos()

        return mBinding.root
    }

    private fun initAdapter(
        adRequest: AdRequest,
        viewModel: PhotoViewModel,
        mBinding: FragmentPhotosBinding
    ) {
        mAdapter = PhotoAdapter(adRequest, ::onPhotoClicked) { viewModel.retry() }
        mBinding.recycler.adapter = mAdapter
        viewModel.photos.observe(viewLifecycleOwner, Observer {
            Log.d("initAdapter", "list: ${it?.size}")
            mBinding.recycler.adapter = mAdapter
            mAdapter.submitList(it)
        })
        viewModel.networkState.observe(viewLifecycleOwner, Observer {
            mAdapter.setNetworkState(it)
        })
    }

    fun onPhotoClicked(photo: Photo?) {
        //TODO
    }

    fun removeBannerAds() {
        //TODO
    }


}
