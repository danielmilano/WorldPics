package dreamlab.worldpics.ui.photo.editorchoise

import androidx.lifecycle.ViewModelProvider
import dreamlab.worldpics.ui.photo.base.BasePhotoViewModel
import dreamlab.worldpics.ui.photo.base.BasePhotosFragment
import dreamlab.worldpics.util.viewModelProvider
import javax.inject.Inject

class EditorChoicePhotosFragment : BasePhotosFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val viewModel: BasePhotoViewModel
        get() = viewModelProvider(viewModelFactory) as EditorChoicePhotoViewModel
}
