package dreamlab.worldpics

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.DispatchingAndroidInjector
import dreamlab.worldpics.base.BaseActivity
import dreamlab.worldpics.base.BaseViewFragmentHelper
import dreamlab.worldpics.databinding.ActivityMainBinding
import dreamlab.worldpics.ui.filter.FilterFragment
import dreamlab.worldpics.ui.photo.PhotosFragment
import javax.inject.Inject

class MainActivity : BaseActivity(), BaseViewFragmentHelper, PhotosFragment.Listener {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    val FRAGMENT_PHOTOS_TAG = "FRAGMENT_PHOTOS"
    val FRAGMENT_DETAIL_TAG = "FRAGMENT_DETAIL"
    val FRAGMENT_PREFERENCES_TAG = "FRAGMENT_PREFERENCES"

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_main, null, false)
        setContentView(mBinding.root)
        mBinding.bottomNavigationBar.setOnNavigationItemSelectedListener(
            mOnNavigationItemSelectedListener
        )
    }

    override fun removeBannerAds() {
        //fragmentWithTag<PhotosFragment>(FRAGMENT_PHOTOS_TAG)?.removeBannerAds()
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.photos -> {
                    mBinding.navigationSwitcher.displayedChild = 0
                    return@OnNavigationItemSelectedListener true
                }
                R.id.hottest -> {
                    //mBinding.navigationSwitcher.displayedChild = 1
                    return@OnNavigationItemSelectedListener true
                }
                R.id.favourites -> {
                    //mBinding.navigationSwitcher.displayedChild = 2
                    return@OnNavigationItemSelectedListener true
                }
                R.id.settings -> {
                    mBinding.navigationSwitcher.displayedChild = 1
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

}
