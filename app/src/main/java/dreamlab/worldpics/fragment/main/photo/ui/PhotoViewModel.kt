package dreamlab.worldpics.fragment.main.photo.ui

import android.net.ConnectivityManager
import androidx.lifecycle.ViewModel
import dreamlab.worldpics.di.CoroutineScopeIO
import dreamlab.worldpics.fragment.main.photo.data.PhotoRepository
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class PhotoViewModel @Inject constructor(
    repository: PhotoRepository,
    @CoroutineScopeIO private val ioCoroutineScope: CoroutineScope
) : ViewModel() {

    @Inject
    lateinit var connectivityManager: ConnectivityManager

    val photos by lazy {
        repository.observePagedPhotos(
            connectivityManager.activeNetworkInfo?.isConnected!!,
            ioCoroutineScope
        )
    }
}
