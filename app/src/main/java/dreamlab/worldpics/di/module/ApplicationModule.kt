package dreamlab.worldpics.di.module

import android.content.Context
import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import dreamlab.worldpics.WorldPics
import dreamlab.worldpics.di.CoroutineScropeIO
import dreamlab.worldpics.di.module.DatabaseModule
import dreamlab.worldpics.di.module.network.NetworkModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Module(includes = [ViewModelModule::class, NetworkModule::class, DatabaseModule::class])
class ApplicationModule {

    @Provides
    fun provideContext(application: WorldPics): Context {
        return application.applicationContext
    }


    @Provides
    fun providesConnectivityManager(context: Context): ConnectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

    @CoroutineScropeIO
    @Provides
    fun provideCoroutineScopeIO() = CoroutineScope(Dispatchers.IO)

}
