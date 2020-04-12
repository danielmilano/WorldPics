package dreamlab.worldpics.base

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import androidx.annotation.StringRes
import android.widget.Toast
import dreamlab.worldpics.BuildConfig
import dreamlab.worldpics.R

/**
 * Created by corbi on 12/04/17.
 */

interface BaseViewAlertHelper {

    fun getBaseViewContext(): Context?

    fun showAlert(@StringRes title: Int = R.string.error, @StringRes msg: Int = R.string.error_message, @StringRes buttonPositive: Int = android.R.string.ok, actionPositive: DialogInterface.OnClickListener? = null, @StringRes buttonNegative: Int? = null, actionNegative: DialogInterface.OnClickListener? = null, dismissListener: DialogInterface.OnDismissListener? = null) {
        val builder = AlertDialog.Builder(getBaseViewContext())
        builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton(buttonPositive, actionPositive)
        if (buttonNegative != null)
            builder.setNegativeButton(buttonNegative, actionNegative)

        builder.create().apply { setOnDismissListener(dismissListener) }.show()
    }

    fun showAlert(title: String, msg: String, buttonPositive: String = getBaseViewContext()!!.getString(android.R.string.ok), actionPositive: DialogInterface.OnClickListener? = null, @StringRes buttonNegative: Int? = null, actionNegative: DialogInterface.OnClickListener? = null, dismissListener: DialogInterface.OnDismissListener? = null) {
        val builder = AlertDialog.Builder(getBaseViewContext())
        builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton(buttonPositive, actionPositive)
        if (buttonNegative != null)
            builder.setNegativeButton(buttonNegative, actionNegative)
        builder.create().apply { setOnDismissListener(dismissListener) }.show()

    }

    fun showGenericAlert() {
        showAlert(R.string.error, R.string.error_message, android.R.string.ok)
    }

    fun showDebugAlert(title: String, message: String) {
        if (BuildConfig.DEBUG)
            showAlert(title, message)
    }

    fun showDebugToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        if (BuildConfig.DEBUG)
            Toast.makeText(getBaseViewContext(), message, duration).show()
    }

    fun showToast(@StringRes message: Int, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(getBaseViewContext(), message, duration).show()
    }

    fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(getBaseViewContext(), message, duration).show()
    }

}
