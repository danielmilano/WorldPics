package dreamlab.worldpics.ui.photo

import android.net.ConnectivityManager
import androidx.lifecycle.ViewModel
import dreamlab.worldpics.di.CoroutineScopeIO
import dreamlab.worldpics.fragment.main.photo.data.PhotoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import javax.inject.Inject

class PhotoViewModel @Inject constructor(
    private val repository: PhotoRepository,
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

    /**
     * Cancel all coroutines when the ViewModel is cleared.
     */
    override fun onCleared() {
        super.onCleared()
        ioCoroutineScope.cancel()
    }
}
