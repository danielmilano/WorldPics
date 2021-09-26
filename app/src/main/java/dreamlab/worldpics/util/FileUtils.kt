package dreamlab.worldpics.util

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.inject.Deferred
import java.net.URL
import java.text.DecimalFormat
import java.util.Random
import dreamlab.worldpics.R
import dreamlab.worldpics.WorldPics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.*
import java.lang.Exception


/**
 * Created by Daniel on 06/11/2017.
 */

object FileUtils {
    private val TAG = FileUtils::class.java.simpleName

    fun getCacheSizeInMB(context: Context): Int {
        var size: Long = 0
        val files = context.cacheDir.listFiles()
        files?.let {
            for (f in it) {
                size += f.length()
            }
        }

        Log.i("CACHE SIZE: ", (size.toInt() / 1000000).toString())
        return size.toInt() / 1000000
    }

    fun getCacheSize(context: Context): String {
        var size: Long = 0
        size += getDirSize(context.cacheDir)
        return readableFileSize(size)
    }

    private fun getDirSize(dir: File): Long {
        var size: Long = 0
        dir.listFiles()?.let {
            for (file in it) {
                if (file != null && file.isDirectory) {
                    size += getDirSize(file)
                } else if (file != null && file.isFile) {
                    size += file.length()
                }
            }
        }
        return size
    }

    private fun readableFileSize(size: Long): String {
        if (size <= 0) return "0 Bytes"
        val units = arrayOf("Bytes", "kB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
    }

    fun downloadPhoto(context: Context, url: String) : Uri? {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "title")
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        uri?.let {
            val outStream: OutputStream?
            try {
                outStream = context.contentResolver.openOutputStream(uri)
                Glide.with(context).asBitmap().load(url)
                    .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get()
                    .compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                outStream?.close()
            } catch (e: Exception) {
                return null
            }
        } ?: return null

        return uri
    }

    fun savePhotoInGallery(context: Context, url: String): Uri? {
        var n = 10000
        n = Random().nextInt(n)
        val filename = "Image-$n.jpg"
        try {
            val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadUri = Uri.parse(url)
            val request = DownloadManager.Request(downloadUri)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(filename)
                .setMimeType("image/jpeg")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_PICTURES,
                    File.separator + WorldPics.TAG + File.separator + filename
                )

            val id = dm.enqueue(request)

            return dm.getUriForDownloadedFile(id)
        } catch (e: Exception) {
            return null
        }
    }
}
