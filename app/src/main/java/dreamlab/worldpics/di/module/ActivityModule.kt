package dreamlab.worldpics.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dreamlab.worldpics.activity.MainActivity
import dreamlab.worldpics.di.module.fragment.main.MainFragmentModule

@Suppress("unused")
@Module
abstract class ActivityModule {
    @ContributesAndroidInjector(modules = [MainFragmentModule::class])
    abstract fun contributeMainActivity(): MainActivity
}
