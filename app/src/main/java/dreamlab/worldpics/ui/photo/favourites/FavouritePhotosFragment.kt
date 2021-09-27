package dreamlab.worldpics.ui.photo.favourites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dreamlab.worldpics.base.BaseFragment
import dreamlab.worldpics.databinding.FragmentBasePhotosBinding
import dreamlab.worldpics.databinding.FragmentFavouritesBinding
import dreamlab.worldpics.model.Photo
import dreamlab.worldpics.util.viewModelProvider
import javax.inject.Inject

class FavouritePhotosFragment :
    BaseFragment<FavouritePhotosFragment.Listener>(Listener::class.java) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var viewModel: FavouritePhotosViewModel? = null

    private var _mBinding: FragmentFavouritesBinding? = null
    private val mBinding get() = _mBinding!!

    private lateinit var mAdapter: FavouritePhotoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _mBinding = FragmentFavouritesBinding.inflate(inflater, container, false)
        viewModel = viewModelProvider(viewModelFactory)
        mAdapter = FavouritePhotoAdapter(::onPhotoClicked)
        mBinding.recycler.adapter = mAdapter
        viewModel?.getFavouritePhotos()?.observe(
            viewLifecycleOwner, {
                mAdapter.setPhotos(ArrayList(it))
                if (it.isEmpty()) {
                    mBinding.emptyPlaceholder.visibility = View.VISIBLE
                } else {
                    mBinding.emptyPlaceholder.visibility = View.GONE
                }
            }
        )

        return mBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _mBinding = null
    }

    private fun onPhotoClicked(photo: Photo) {
        mListenerHelper.listener?.onPhotoClicked(photo)
    }

    interface Listener {
        fun onPhotoClicked(photo: Photo)
    }

}
