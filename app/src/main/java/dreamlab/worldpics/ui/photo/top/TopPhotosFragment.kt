package dreamlab.worldpics.ui.photo.top

import androidx.lifecycle.ViewModelProvider
import dreamlab.worldpics.ui.photo.base.BasePhotoViewModel
import dreamlab.worldpics.ui.photo.base.BasePhotosFragment
import dreamlab.worldpics.util.viewModelProvider
import javax.inject.Inject

class TopPhotosFragment : BasePhotosFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val viewModel: BasePhotoViewModel
        get() = viewModelProvider(viewModelFactory) as TopPhotoViewModel
}
