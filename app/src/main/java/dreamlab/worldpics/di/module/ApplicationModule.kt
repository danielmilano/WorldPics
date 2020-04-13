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
import dreamlab.worldpics.data.ApplicationDatabase
import dreamlab.worldpics.di.CoroutineScopeIO
import dreamlab.worldpics.fragment.main.photo.data.PhotoRemoteDataSource
import dreamlab.worldpics.network.service.PhotoService
import dreamlab.worldpics.util.SharedPreferenceStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ApplicationModule {

    @Provides
    fun provideContext(application: WorldPics): Context {
        return application.applicationContext
    }

    @Singleton
    @Provides
    fun providesPreferenceStorage(context: Context) =
        SharedPreferenceStorage(context)

    @Provides
    fun providesConnectivityManager(context: Context): ConnectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

    @CoroutineScopeIO
    @Provides
    fun provideCoroutineScopeIO() = CoroutineScope(Dispatchers.IO)

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)

    @Provides
    fun provideOkHttpClient(
        client: OkHttpClient
    ): OkHttpClient {
        val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_2)
            .cipherSuites(
                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
            )
            .build()

        return client.newBuilder().connectionSpecs((listOf<ConnectionSpec>(spec))).build()
    }

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
    fun providePhotoSetRemoteDataSource(photoService: PhotoService) =
        PhotoRemoteDataSource(photoService)

    @Singleton
    @Provides
    fun provideDb(context: Context) = ApplicationDatabase.getInstance(context)

    @Singleton
    @Provides
    fun providePhotoDao(db: ApplicationDatabase) = db.photoDao()

    @Singleton
    @Provides
    fun provideAdRequestBuilder(context: Context) : AdRequest.Builder {
        val builder = AdRequest.Builder()

        val extras = Bundle()
        extras.putBoolean("is_designed_for_families", true)

        if (ConsentInformation.getInstance(context).consentStatus == ConsentStatus.NON_PERSONALIZED) {
            extras.putString("npa", "1")
            builder.addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
        }

        return builder
    }

}
