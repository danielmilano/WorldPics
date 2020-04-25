package dreamlab.worldpics.di.module

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dreamlab.worldpics.di.ViewModelKey
import dreamlab.worldpics.di.scope.FragmentScoped
import dreamlab.worldpics.ui.detail.DetailFragment
import dreamlab.worldpics.ui.filter.FilterFragment
import dreamlab.worldpics.ui.photo.search.SearchPhotoViewModel
import dreamlab.worldpics.ui.photo.search.SearchPhotosFragment
import dreamlab.worldpics.ui.photo.top.TopPhotoViewModel
import dreamlab.worldpics.ui.photo.top.TopPhotosFragment
import dreamlab.worldpics.ui.settings.SettingsFragment
import dreamlab.worldpics.ui.settings.SettingsPreferenceFragment
import dreamlab.worldpics.ui.detail.DetailViewModel
import dreamlab.worldpics.ui.photo.favourites.FavouritePhotosFragment
import dreamlab.worldpics.ui.photo.favourites.FavouritePhotosViewModel
import dreamlab.worldpics.ui.settings.SettingsViewModel

@Suppress("UNUSED")
@Module
abstract class MainModule {

    /**
     * Generates an [AndroidInjector] for the [SearchPhotosFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun contributeSearchPhotosFragment(): SearchPhotosFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [SearchPhotoViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(SearchPhotoViewModel::class)
    abstract fun bindSearchPhotoViewModel(viewModel: SearchPhotoViewModel): ViewModel

    /**
     * Generates an [AndroidInjector] for the [TopPhotosFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun contributeTopPhotosFragment(): TopPhotosFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [TopPhotoViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(TopPhotoViewModel::class)
    abstract fun bindTopPhotoViewModel(viewModel: TopPhotoViewModel): ViewModel

    /**
     * Generates an [AndroidInjector] for the [FilterFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun contributeFilterFragment(): FilterFragment

    /**
     * Generates an [AndroidInjector] for the [PreferenceFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun contributeSettingsFragment(): SettingsFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [SettingsViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(viewModel: SettingsViewModel): ViewModel

    /**
     * Generates an [AndroidInjector] for the [SettingsPreferenceFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun contributeSettingsPreferenceFragment(): SettingsPreferenceFragment

    /**
     * Generates an [AndroidInjector] for the [DetailFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun contributeDetailFragment(): DetailFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [DetailViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(DetailViewModel::class)
    abstract fun bindDetailViewModel(viewModel: DetailViewModel): ViewModel

    /**
     * Generates an [AndroidInjector] for the [FavouritePhotosFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun contributeFavouritePhotosFragment(): FavouritePhotosFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [FavouritePhotosViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(FavouritePhotosViewModel::class)
    abstract fun bindFavouritePhotosViewModel(viewModel: FavouritePhotosViewModel): ViewModel

}
