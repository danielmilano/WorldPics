package dreamlab.worldpics.di.module.network.service

import dagger.Module
import dagger.Provides
import dreamlab.worldpics.fragment.main.photo.data.PhotoRemoteDataSource
import dreamlab.worldpics.network.service.PhotoService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class PhotoServiceModule {

    @Singleton
    @Provides
    fun providePhotoService(
        converterFactory: GsonConverterFactory
    ): PhotoService {
        return Retrofit.Builder()
            .baseUrl(PhotoService.BASE_URL)
            .addConverterFactory(converterFactory)
            .client(OkHttpClient())
            .build().create(PhotoService::class.java)
    }

    @Singleton
    @Provides
    fun providePhotoSetRemoteDataSource(legoService: PhotoService) =
        PhotoRemoteDataSource(legoService)
}