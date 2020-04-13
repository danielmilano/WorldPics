package dreamlab.worldpics.di.module

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dreamlab.worldpics.di.ViewModelFactory

@Suppress("unused")
@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
