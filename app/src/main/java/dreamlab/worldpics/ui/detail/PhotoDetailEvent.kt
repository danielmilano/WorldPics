package dreamlab.worldpics.ui.detail

import android.net.Uri
import dreamlab.worldpics.model.Photo

sealed class PhotoDetailEvent {
    object Loading : PhotoDetailEvent()
    object Completed : PhotoDetailEvent()
    object Error : PhotoDetailEvent()
    data class OnPhotoDetailAlreadyFavourite(val photo : Photo) : PhotoDetailEvent()
    data class Download(val uri: Uri) : PhotoDetailEvent()
    data class SetPhotoDetail(val uri: Uri) : PhotoDetailEvent()
    data class Share(val uri: Uri) : PhotoDetailEvent()
    data class Edit(val uri: Uri) : PhotoDetailEvent()
}

