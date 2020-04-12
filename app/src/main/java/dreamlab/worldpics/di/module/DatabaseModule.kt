package dreamlab.worldpics.di.module

import android.app.Application
import dagger.Module
import dagger.Provides
import dreamlab.worldpics.data.ApplicationDatabase
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideDb(app: Application) = ApplicationDatabase.getInstance(app)

    @Singleton
    @Provides
    fun providePhotoDao(db: ApplicationDatabase) = db.photoDao()
}