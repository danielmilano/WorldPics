package dreamlab.worldpics.di.module.fragment.main

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dreamlab.worldpics.fragment.main.photo.ui.PhotosFragment

@Suppress("unused")
@Module
abstract class MainFragmentModule {
    @ContributesAndroidInjector
    abstract fun contributePhotoFragment(): PhotosFragment
}
