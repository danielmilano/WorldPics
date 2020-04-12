package dreamlab.worldpics.fragment.main.photo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.elifox.legocatalog.data.Result
import com.google.ads.consent.ConsentInformation
import com.google.ads.consent.ConsentStatus
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.material.snackbar.Snackbar
import dreamlab.worldpics.WorldPics
import dreamlab.worldpics.adapter.PhotoAdapter
import dreamlab.worldpics.base.BaseFragment
import dreamlab.worldpics.databinding.FragmentPhotosBinding
import dreamlab.worldpics.di.Injectable
import dreamlab.worldpics.fragment.main.photo.data.Photo
import dreamlab.worldpics.util.injectViewModel
import javax.inject.Inject

class PhotosFragment : BaseFragment<Void>(), Injectable, PhotoAdapter.Listener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: PhotoViewModel

    private lateinit var mBinding: FragmentPhotosBinding
    private lateinit var mAdapter: PhotoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var builder: AdRequest.Builder? = null

        if (!WorldPics.isPremium) {
            builder = AdRequest.Builder()

            val extras = Bundle()
            extras.putBoolean("is_designed_for_families", true)

            if (ConsentInformation.getInstance(activity).consentStatus == ConsentStatus.NON_PERSONALIZED) {
                extras.putString("npa", "1")
                builder.addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
            }
        }

        mAdapter = PhotoAdapter(context!!, ArrayList(), Glide.with(this), builder, this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        viewModel = injectViewModel(viewModelFactory)

        mBinding = FragmentPhotosBinding.inflate(inflater, container, false)
        mBinding.recycler.adapter = mAdapter

        subscribeUi(mBinding)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onPhotoClick(photo: Photo) {
        //TODO
    }

    fun removeBannerAds() {
        //TODO
    }

    private fun subscribeUi(binding: FragmentPhotosBinding) {
        viewModel.photos.observe(viewLifecycleOwner, Observer { result ->
            when (result.status) {
                Result.Status.SUCCESS -> {
                    //binding.progressBar.hide()
                    result.data?.let {
                        mAdapter.setValues(ArrayList(it))
                    }
                }
                Result.Status.LOADING -> {}//binding.progressBar.show()
                Result.Status.ERROR -> {
                    //binding.progressBar.hide()
                    Snackbar.make(this@PhotosFragment.view!!, result.message!!, Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }
}
