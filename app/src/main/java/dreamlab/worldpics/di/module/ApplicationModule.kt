package dreamlab.worldpics.di.module

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import com.google.ads.consent.ConsentInformation
import com.google.ads.consent.ConsentStatus
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dreamlab.worldpics.WorldPics
import dreamlab.worldpics.repository.PhotoRepository
import dreamlab.worldpics.db.WorldPicsDatabase
import dreamlab.worldpics.api.PhotoApi
import dreamlab.worldpics.util.SharedPreferenceStorage
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
class ApplicationModule {

    @Provides
    fun provideContext(application: WorldPics): Context {
        return application.applicationContext
    }

    @Provides
    fun provideApplication(application: WorldPics) : Application{
        return application
    }

    @Singleton
    @Provides
    fun providesPreferenceStorage(context: Context) =
        SharedPreferenceStorage(context)

    @Provides
    fun providesConnectivityManager(context: Context): ConnectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_2)
            .cipherSuites(
                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
            )
            .build()

        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient().newBuilder().addInterceptor(logger)
            .connectionSpecs((listOf(spec))).build()
    }

    @Singleton
    @Provides
    fun providePhotoService(
        converterFactory: GsonConverterFactory
    ): PhotoApi {
        return Retrofit.Builder()
            .baseUrl(PhotoApi.BASE_URL)
            .addConverterFactory(converterFactory)
            .client(OkHttpClient())
            .build().create(PhotoApi::class.java)
    }

    @Singleton
    @Provides
    fun provideDb(context: Context) = WorldPicsDatabase.getInstance(context)

    @Singleton
    @Provides
    fun providePhotoDao(db: WorldPicsDatabase) = db.photoDao()

    @Singleton
    @Provides
    fun providePhotoRepository(photoApi: PhotoApi) =
        PhotoRepository(photoApi, Executors.newFixedThreadPool(5))

    @Singleton
    @Provides
    fun provideAdRequest(context: Context): AdRequest {
        val builder = AdRequest.Builder()

        val extras = Bundle()
        extras.putBoolean("is_designed_for_families", true)

        if (ConsentInformation.getInstance(context).consentStatus == ConsentStatus.NON_PERSONALIZED) {
            extras.putString("npa", "1")
            builder.addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
        }

        return builder.build()
    }
}
