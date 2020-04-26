package dreamlab.worldpics.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import java.io.File


/**
 * Android system intents
 */

fun intentShareText(activity: Activity, text: String) {
    val shareIntent = ShareCompat.IntentBuilder.from(activity)
        .setText(text)
        .setType("text/plain")
        .createChooserIntent()
        .apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // If we're on Lollipop, we can open the intent as a document
                addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            } else {
                // Else, we will use the old CLEAR_WHEN_TASK_RESET flag
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
            }
        }
    activity.startActivity(shareIntent)
}

fun intentOpenWebsite(activity: Activity, url: String) {
    val openURL = Intent(Intent.ACTION_VIEW)
    openURL.data = Uri.parse(url)
    activity.startActivity(openURL)
}

fun intentSharePhoto(uri: Uri): Intent {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "image/jpeg"
    intent.putExtra(Intent.EXTRA_STREAM, uri)
    return intent
}

fun intentSetPhotoAs(uri: Uri): Intent {
    val intent = Intent(Intent.ACTION_ATTACH_DATA)
    intent.addCategory(Intent.CATEGORY_DEFAULT)
    intent.setDataAndType(uri, "image/*")
    intent.putExtra("mimeType", "image/*")
    return intent
}

fun intentEditPhoto(uri: Uri): Intent {
    val editIntent = Intent(Intent.ACTION_EDIT)
    editIntent.setDataAndType(uri, "image/*")
    editIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
    editIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    return editIntent
}
