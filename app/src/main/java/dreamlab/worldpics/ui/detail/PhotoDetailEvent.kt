package dreamlab.worldpics.ui.detail

import android.net.Uri
import dreamlab.worldpics.model.Photo

sealed class PhotoDetailEvent {
    object Loading : PhotoDetailEvent()
    object Error : PhotoDetailEvent()
    data class IsFavourite(val photo : Photo?) : PhotoDetailEvent()
    data class Downloaded(val uri: Uri) : PhotoDetailEvent()
    data class SetPhotoAs(val uri: Uri) : PhotoDetailEvent()
    data class Share(val uri: Uri) : PhotoDetailEvent()
    data class Edit(val uri: Uri) : PhotoDetailEvent()
}

