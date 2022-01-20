package dreamlab.worldpics.binding

import android.animation.ObjectAnimator
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.ViewPropertyTransition
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dreamlab.worldpics.model.Photo

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("isGone")
fun bindIsGone(view: FloatingActionButton, isGone: Boolean?) {
    if (isGone == null || isGone) {
        view.hide()
    } else {
        view.show()
    }
}

@BindingAdapter("webFormatImageFromUrl")
fun bindWebFormatImageFromUrl(view: ImageView, photo: Photo) {
    val fadeAnimation =
        ViewPropertyTransition.Animator {
            val fadeAnim = ObjectAnimator.ofFloat(it, "alpha", 0f, 1f)
            fadeAnim.duration = 500
            fadeAnim.start()
        }

    Glide.with(view)
        .load(photo.webformatURL)
        .transition(GenericTransitionOptions.with(fadeAnimation))
        .override(photo.webformatWidth?.toInt()!!, photo.webformatHeight?.toInt()!!)
        .into(view)
}

@BindingAdapter("imageFromByteArray")
fun bindImageFromByteArray(view: ImageView, photo: Photo) {
    val fadeAnimation =
        ViewPropertyTransition.Animator {
            val fadeAnim = ObjectAnimator.ofFloat(it, "alpha", 0f, 1f)
            fadeAnim.duration = 500
            fadeAnim.start()
        }

    Glide.with(view)
        .load(photo.imageBlob)
        .transition(GenericTransitionOptions.with(fadeAnimation))
        .into(view)
}

@BindingAdapter("largeImageFromUrl")
fun bindLargeImageFromUrl(view: ImageView, url: String) {
    val fadeAnimation =
        ViewPropertyTransition.Animator {
            val fadeAnim = ObjectAnimator.ofFloat(it, "alpha", 0f, 1f)
            fadeAnim.duration = 500
            fadeAnim.start()
        }

    Glide.with(view)
        .load(url)
        .transition(GenericTransitionOptions.with(fadeAnimation))
        .into(view)
}


@BindingAdapter("renderHtml")
fun bindRenderHtml(view: TextView, description: String?) {
    if (description != null) {
        view.text = HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_COMPACT)
        view.movementMethod = LinkMovementMethod.getInstance()
    } else {
        view.text = ""
    }
}


