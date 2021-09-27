package dreamlab.worldpics

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dreamlab.worldpics.base.Anim
import dreamlab.worldpics.base.BaseActivity
import dreamlab.worldpics.base.BaseViewFragmentHelper
import dreamlab.worldpics.databinding.ActivityMainBinding
import dreamlab.worldpics.model.Photo
import dreamlab.worldpics.model.PhotoRequest
import dreamlab.worldpics.ui.detail.DetailFragment
import dreamlab.worldpics.ui.filter.FilterFragment
import dreamlab.worldpics.ui.photo.base.BasePhotosFragment
import dreamlab.worldpics.ui.photo.favourites.FavouritePhotosFragment
import dreamlab.worldpics.ui.photo.search.SearchPhotosFragment
import dreamlab.worldpics.ui.photo.editorchoise.EditorChoicePhotosFragment
import dreamlab.worldpics.ui.settings.SettingsFragment

class MainActivity : BaseActivity(), BaseViewFragmentHelper, BasePhotosFragment.Listener,
    FilterFragment.Listener, DetailFragment.Listener, FavouritePhotosFragment.Listener {

    val SEARCH_PHOTOS_FRAGMENT_TAG = "SEARCH_PHOTOS_FRAGMENT"
    val TOP_PHOTOS_FRAGMENT_TAG = "TOP_PHOTOS_FRAGMENT"
    val FAVOURITE_PHOTOS_FRAGMENT_TAG = "FAVOURITE_PHOTOS_FRAGMENT"
    val SETTINGS_FRAGMENT_TAG = "SETTINGS_FRAGMENT"

    private enum class DisplayedFragment(val id: Int) {
        SEARCH(0),
        FAVOURITES(1),
        SETTINGS(2),
        TOP(3)
    }

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_main, null, false)
        setContentView(mBinding.root)
        mBinding.bottomNavigationBar.setOnNavigationItemSelectedListener(
            mOnNavigationItemSelectedListener
        )
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.photos -> {
                    mBinding.navigationSwitcher.displayedChild = DisplayedFragment.SEARCH.id
                    return@OnNavigationItemSelectedListener true
                }
                /*R.id.hottest -> {
                    mBinding.navigationSwitcher.displayedChild = DisplayedFragment.TOP.id
                    return@OnNavigationItemSelectedListener true
                }*/
                R.id.favourites -> {
                    mBinding.navigationSwitcher.displayedChild = DisplayedFragment.FAVOURITES.id
                    return@OnNavigationItemSelectedListener true
                }
                R.id.info -> {
                    fragmentWithTag<SettingsFragment>(SETTINGS_FRAGMENT_TAG)?.updateCacheSummary()
                    mBinding.navigationSwitcher.displayedChild = DisplayedFragment.SETTINGS.id
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onShowFiltersClick() {
        (supportFragmentManager
            .fragmentFactory
            .instantiate(
                ClassLoader.getSystemClassLoader(),
                FilterFragment::class.java.name
            ) as DialogFragment).show(
            supportFragmentManager,
            FilterFragment.FilterPreferenceFragment::class.java.name
        )
    }

    override fun onPhotoClicked(photo: Photo) {
        replacePushFragment(
            mBinding.container.id,
            DetailFragment.newInstance(photo),
            anim = Anim.PUSH
        )
    }

    override fun onResetFilters() {
        when (mBinding.navigationSwitcher.displayedChild) {
            DisplayedFragment.SEARCH.id -> {
                fragmentWithTag<SearchPhotosFragment>(SEARCH_PHOTOS_FRAGMENT_TAG)?.onResetFilters()
            }
            DisplayedFragment.TOP.id -> {
                fragmentWithTag<EditorChoicePhotosFragment>(TOP_PHOTOS_FRAGMENT_TAG)?.onResetFilters()
            }
        }

    }

    override fun onApplyFilters(request: PhotoRequest) {
        when (mBinding.navigationSwitcher.displayedChild) {
            DisplayedFragment.SEARCH.id -> {
                fragmentWithTag<SearchPhotosFragment>(SEARCH_PHOTOS_FRAGMENT_TAG)?.onApplyFilters(
                    request
                )
            }
            DisplayedFragment.TOP.id -> {
                fragmentWithTag<EditorChoicePhotosFragment>(TOP_PHOTOS_FRAGMENT_TAG)?.onApplyFilters(request)
            }
        }
    }
}
