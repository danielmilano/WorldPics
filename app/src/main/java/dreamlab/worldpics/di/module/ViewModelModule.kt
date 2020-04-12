package dreamlab.worldpics.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dreamlab.worldpics.di.ViewModelFactory
import dreamlab.worldpics.di.ViewModelKey

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dreamlab.worldpics.fragment.main.photo.ui.PhotoViewModel

@Suppress("unused")
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(PhotoViewModel::class)
    abstract fun bindPhotoViewModel(viewModel: PhotoViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
