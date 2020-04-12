package dreamlab.worldpics.billing

import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.android.billingclient.api.Purchase
import dreamlab.worldpics.WorldPics

/**
 * Handles control logic of the MainActivity
 */
class BillingViewController(private val activity: FragmentActivity) {


    val updateListener: UpdateListener

    init {
        updateListener = UpdateListener()
    }

    /**
     * Handler to billing updates
     */
    inner class UpdateListener : BillingManager.BillingUpdatesListener {
        override fun onBillingClientSetupFinished() {

        }

        override fun onConsumeFinished(token: String, result: Int) {}

        override fun onPurchasesUpdated(purchases: List<Purchase>) {

            for (purchase in purchases) {
                when (purchase.sku) {
                    "ad_free" -> {
                        Log.d(TAG, "You are Premium! Congratulations!!!")
                        WorldPics.isPremium = true
                    }
                }
            }
        }
    }

    companion object {
        private val TAG = "MainActivity"
    }
}