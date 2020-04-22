package dreamlab.worldpics

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dreamlab.worldpics.base.BaseActivity
import dreamlab.worldpics.base.BaseViewFragmentHelper
import dreamlab.worldpics.databinding.ActivityMainBinding
import dreamlab.worldpics.model.PhotoRequest
import dreamlab.worldpics.ui.filter.FilterFragment
import dreamlab.worldpics.ui.photo.search.SearchPhotosFragment
import dreamlab.worldpics.ui.photo.top.TopPhotosFragment

class MainActivity : BaseActivity(), BaseViewFragmentHelper, SearchPhotosFragment.Listener,
    TopPhotosFragment.Listener,
    FilterFragment.Listener {

    val SEARCH_PHOTOS_FRAGMENT_TAG = "SEARCH_PHOTOS_FRAGMENT"
    val TOP_PHOTOS_FRAGMENT_TAG = "TOP_PHOTOS_FRAGMENT"
    val FAVOURITE_PHOTOS_FRAGMENT_TAG = "TOP_PHOTOS_FRAGMENT"
    val SETTINGS_FRAGMENT_TAG = "SETTINGS_FRAGMENT"

    private enum class DisplayedFragment(val id: Int) {
        SEARCH(0),
        TOP(1),
        //FAVOURITES(2),
        SETTINGS(2)
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
                R.id.hottest -> {
                    mBinding.navigationSwitcher.displayedChild = DisplayedFragment.TOP.id
                    return@OnNavigationItemSelectedListener true
                }
                R.id.favourites -> {
                    //mBinding.navigationSwitcher.displayedChild = DisplayedFragment.FAVOURITES.id
                    return@OnNavigationItemSelectedListener true
                }
                R.id.settings -> {
                    mBinding.navigationSwitcher.displayedChild = DisplayedFragment.SETTINGS.id
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onFiltersClick() {
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

    override fun onResetFilters() {
        when (mBinding.navigationSwitcher.displayedChild) {
            DisplayedFragment.SEARCH.id -> {
                fragmentWithTag<SearchPhotosFragment>(SEARCH_PHOTOS_FRAGMENT_TAG)?.onResetFilters()
            }
            DisplayedFragment.TOP.id -> {
                fragmentWithTag<TopPhotosFragment>(TOP_PHOTOS_FRAGMENT_TAG)?.onResetFilters()
            }
        }

    }

    override fun onApplyFilters(request: PhotoRequest) {
        when (mBinding.navigationSwitcher.displayedChild) {
            0 -> {
                fragmentWithTag<SearchPhotosFragment>(SEARCH_PHOTOS_FRAGMENT_TAG)?.onApplyFilters(
                    request
                )
            }
            1 -> {
                fragmentWithTag<TopPhotosFragment>(TOP_PHOTOS_FRAGMENT_TAG)?.onApplyFilters(request)
            }
        }
    }

}
