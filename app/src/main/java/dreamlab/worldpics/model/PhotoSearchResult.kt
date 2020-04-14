package dreamlab.worldpics.model

import androidx.lifecycle.LiveData
import androidx.paging.PagedList

data class PhotoSearchResult(
    val data: LiveData<PagedList<Photo?>>,
    val networkErrors: LiveData<String>
)
