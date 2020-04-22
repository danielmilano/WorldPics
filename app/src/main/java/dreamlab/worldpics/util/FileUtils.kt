package dreamlab.worldpics.util

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import java.net.URL
import java.text.DecimalFormat
import java.util.Random
import dreamlab.worldpics.R
import dreamlab.worldpics.WorldPics
import java.io.*
import java.lang.Exception


/**
 * Created by Daniel on 06/11/2017.
 */

object FileUtils {
    private val TAG = FileUtils::class.java.simpleName

    const val WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 1

    fun isStoragePermissionGranted(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted")
                return true
            } else {

                Log.v(TAG, "Permission is revoked")

                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST)
                return false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted")
            return true
        }
    }

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

    fun setAsWallpaper(context: Context, url: String): Intent? {
        context.let {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "title")
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/*")
            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            uri?.let {
                val outstream: OutputStream?
                try {
                    outstream = context.contentResolver.openOutputStream(uri)
                    Glide.with(context).asBitmap().load(url).submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get().compress(Bitmap.CompressFormat.JPEG, 100, outstream)
                    outstream?.close()
                } catch (e: Exception) {
                    return null
                }
            } ?: return null

            val intent = Intent(Intent.ACTION_ATTACH_DATA)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.setDataAndType(uri, "image/*")
            intent.putExtra("mimeType", "image/*")

            return intent
        }
    }

    fun saveImageInGallery(context: Context, url: String): Boolean {
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
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,
                            File.separator + WorldPics.TAG + File.separator + filename)
            
            dm.enqueue(request)

            return true
        } catch (e : Exception){
            return false
        }
    }

    fun saveImageToStorage(context: Context, url: String): String? {
        val imageUrl: URL
        val bitmap: Bitmap

        try {
            imageUrl = URL(url)
            bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream())
        } catch (e: IOException) {
            Log.e(TAG, "Error occurred while retrieving image url")
            Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_SHORT).show()
            return null
        }

        var n = 10000
        n = Random().nextInt(n)
        val filename = "Image-$n.jpg"

        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val f = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + File.separator + filename)
        try {
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
        } catch (e: IOException) {
            Log.e(TAG, "Error occurred while creating file to save")
        }

        return f.absolutePath
    }

    fun shareImage(context: Context, url: String): Intent? {
        val imageUrl: URL
        val bitmap: Bitmap
        val share = Intent(Intent.ACTION_SEND)
        val path: String
        try {
            imageUrl = URL(url)
            bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream())

            share.type = "image/jpeg"
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + File.separator + "temporary_file.jpg"
            val f = File(path)
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
        } catch (e: IOException) {
            Log.e(TAG, "Error occurred while retrieving image url")
            return null
        }

        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(path))
        return share
    }

}
