package dreamlab.worldpics.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

object PermissionUtils {
    enum class RequestCodeType(val id: Int) {
        DOWNLOAD_IMAGE_REQUEST_CODE(1),
        SET_IMAGE_AS_REQUEST_CODE(2),
        SHARE_IMAGE_REQUEST_CODE(3)
    }

    fun isStoragePermissionGranted(activity: Activity, requestCode: RequestCodeType): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    requestCode.id
                )
                return false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true
        }
    }

    fun isStoragePermissionGranted(
        activity: Activity,
        fragment: Fragment,
        requestCode: RequestCodeType
    ): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true
            } else {
                fragment.requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    requestCode.id
                )
                return false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true
        }
    }


}