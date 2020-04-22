package dreamlab.worldpics.di.module

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dreamlab.worldpics.di.ViewModelKey
import dreamlab.worldpics.di.scope.FragmentScoped
import dreamlab.worldpics.ui.filter.FilterFragment
import dreamlab.worldpics.ui.photo.PhotoViewModel
import dreamlab.worldpics.ui.photo.PhotosFragment
import dreamlab.worldpics.ui.settings.SettingsFragment
import dreamlab.worldpics.ui.settings.SettingsPreferenceFragment
import dreamlab.worldpics.ui.settings.SettingsViewModel

@Suppress("UNUSED")
@Module
abstract class MainModule {

    /**
     * Generates an [AndroidInjector] for the [PhotosFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun contributePhotosFragment(): PhotosFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [PhotoViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(PhotoViewModel::class)
    abstract fun bindPhotoViewModel(viewModel: PhotoViewModel): ViewModel

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
     * Generates an [AndroidInjector] for the [SettingsPreferenceFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun contributeSettingsPreferenceFragment(): SettingsPreferenceFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [SettingsViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(viewModel: SettingsViewModel): ViewModel

}
