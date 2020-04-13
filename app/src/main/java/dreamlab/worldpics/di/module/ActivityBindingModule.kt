package dreamlab.worldpics.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dreamlab.worldpics.MainActivity
import dreamlab.worldpics.di.scope.ActivityScoped

@Suppress("UNUSED")
@Module
abstract class ActivityBindingModule {
    
    @ActivityScoped
    @ContributesAndroidInjector( modules = [MainModule::class])
    abstract fun contributeMainActivity(): MainActivity
}
