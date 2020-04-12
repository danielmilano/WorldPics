package dreamlab.worldpics.di.component

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dreamlab.worldpics.WorldPics
import dreamlab.worldpics.di.module.ApplicationModule
import dreamlab.worldpics.di.module.ActivityModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, ApplicationModule::class, ActivityModule::class])
interface ApplicationComponent {

    fun inject(application: WorldPics)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }
}
