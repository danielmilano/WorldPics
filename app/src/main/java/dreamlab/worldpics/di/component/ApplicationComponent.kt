package dreamlab.worldpics.di.component

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dreamlab.worldpics.WorldPics
import dreamlab.worldpics.di.module.ActivityBindingModule
import dreamlab.worldpics.di.module.ApplicationModule
import dreamlab.worldpics.di.module.ViewModelModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ApplicationModule::class,
        ViewModelModule::class,
        ActivityBindingModule::class
    ]
)

interface ApplicationComponent : AndroidInjector<WorldPics> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: WorldPics): ApplicationComponent
    }
}
