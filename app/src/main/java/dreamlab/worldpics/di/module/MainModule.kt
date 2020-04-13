package dreamlab.worldpics.di.module

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dreamlab.worldpics.di.ViewModelKey
import dreamlab.worldpics.di.scope.FragmentScoped
import dreamlab.worldpics.ui.photo.PhotoViewModel
import dreamlab.worldpics.ui.photo.PhotosFragment

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
}
