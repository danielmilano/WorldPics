package dreamlab.worldpics.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingsViewModel @Inject constructor() : ViewModel() {

    fun clearCache(context: Context?) {
        context?.let {
            viewModelScope.launch {
                asyncAwait {
                    withContext(Dispatchers.IO) {
                        Glide.get(context).clearDiskCache()
                    }
                    withContext(Dispatchers.Main) {
                        Glide.get(context).clearMemory()
                    }
                }
            }
        }
    }

    private suspend fun <T> asyncAwait(block: suspend CoroutineScope.() -> T): T {
        return withContext(viewModelScope.coroutineContext + Dispatchers.Default) { block() }
    }
}