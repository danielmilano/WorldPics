package dreamlab.worldpics.fragment.main.photo.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class PhotoPageDataSourceFactory @Inject constructor(
    private val dataSource: PhotoRemoteDataSource,
    private val dao: PhotoDao,
    private val scope: CoroutineScope
) : DataSource.Factory<Int, Photo>() {

    private val liveData = MutableLiveData<PhotoPageDataSource>()

    override fun create(): DataSource<Int, Photo> {
        val source = PhotoPageDataSource(dataSource, dao, scope)
        liveData.postValue(source)
        return source
    }

    companion object {
        private const val PAGE_SIZE = 100

        fun pagedListConfig() = PagedList.Config.Builder()
            .setInitialLoadSizeHint(PAGE_SIZE)
            .setPageSize(PAGE_SIZE)
            .setEnablePlaceholders(true)
            .build()
    }

}