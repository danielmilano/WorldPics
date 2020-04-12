package dreamlab.worldpics.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode.OK
import com.android.billingclient.api.BillingClient.BillingResponseCode.USER_CANCELED
import java.util.*

class BillingManager(private val mActivity: Activity, private val mBillingUpdatesListener: BillingUpdatesListener) : PurchasesUpdatedListener {

    /** A reference to BillingClient  */
    private var mBillingClient: BillingClient? = null

    /**
     * True if billing service is connected now.
     */
    private var mIsServiceConnected: Boolean = false

    private val mPurchases = ArrayList<Purchase>()

    /**
     * Returns the value Billing client response code or BILLING_MANAGER_NOT_INITIALIZED if the
     * clien connection response was not received yet.
     */
    var billingClientResponseCode = BILLING_MANAGER_NOT_INITIALIZED
        private set

    val context: Context
        get() = mActivity

    /* BASE_64_ENCODED_PUBLIC_KEY should be YOUR APPLICATION'S PUBLIC KEY
     * (that you got from the Google Play developer console). This is not your
     * developer public key, it's the *app-specific* public key.
     *
     * Instead of just storing the entire literal string here embedded in the
     * program,  construct the key at runtime from pieces or
     * use bit manipulation (for example, XOR with some other string) to hide
     * the actual key.  The key itself is not secret information, but we don't
     * want to make it easy for an attacker to replace the public key with one
     * of their own and then fake messages from the server.
     */

    /**
     * Listener to the updates that happen when purchases list was updated or consumption of the
     * item was finished
     */
    interface BillingUpdatesListener {
        fun onBillingClientSetupFinished()
        fun onConsumeFinished(token: String, result: Int)
        fun onPurchasesUpdated(purchases: List<Purchase>)
    }

    /**
     * Listener for the Billing client state to become connected
     */
    interface ServiceConnectedListener {
        fun onServiceConnected(resultCode: Int)
    }

    init {
        Log.d(TAG, "Creating Billing client.")
        mBillingClient = BillingClient.newBuilder(mActivity).setListener(this).enablePendingPurchases().build()

        Log.d(TAG, "Starting setup.")

        // Start setup. This is asynchronous and the specified listener will be called
        // once setup completes.
        // It also starts to report all the new purchases through onPurchasesUpdated() callback.
        startServiceConnection(Runnable {
            // Notifying the listener that billing client is ready
            mBillingUpdatesListener.onBillingClientSetupFinished()
            // IAB is fully set up. Now, let's get an inventory of stuff we own.
            Log.d(TAG, "Setup successful. Querying inventory.")
            queryPurchases()
        })
    }

    /**
     * Handle a callback that purchases were updated from the Billing library
     */
    override fun onPurchasesUpdated(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {
        when (billingResult?.responseCode) {
            OK -> {
                purchases?.let {
                    for (purchase in purchases) {
                        handlePurchase(purchase)
                    }
                }
                mBillingUpdatesListener.onPurchasesUpdated(mPurchases)
            }
            USER_CANCELED -> {
                Log.i(TAG, "onPurchasesUpdated() - user cancelled the purchase flow - skipping")
            }
            else -> {
                Log.w(TAG, "onPurchasesUpdated() got unknown resultCode: ${billingResult?.responseCode}")
            }
        }
    }

    /**
     * Start a purchase flow
     */
    fun initiatePurchaseFlow(skuId: String, @BillingClient.SkuType billingType: String) {
        val skuList = ArrayList<String>()
        skuList.add(skuId)
        val params = SkuDetailsParams.newBuilder().setSkusList(skuList).setType(billingType).build()

        mBillingClient?.querySkuDetailsAsync(params) { _, skuDetailsList ->
            val flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetailsList[0])
                    .build()
            val purchaseFlowRequest = Runnable {
                mBillingClient?.launchBillingFlow(mActivity, flowParams)
            }

            executeServiceRequest(purchaseFlowRequest)
        }
    }

    /**
     * Clear the resources
     */
    fun destroy() {
        Log.d(TAG, "Destroying the manager.")

        mBillingClient?.let {
            if (it.isReady) {
                mBillingClient?.endConnection()
                mBillingClient = null
            }
        }
    }

    private fun querySkuDetailsAsync(@BillingClient.SkuType itemType: String, skuList: List<String>,
                                     listener: SkuDetailsResponseListener) {
        // Creating a runnable from the request to use it inside our connection retry policy below
        val queryRequest = Runnable {
            // Query the purchase async
            val params = SkuDetailsParams.newBuilder()
            params.setSkusList(skuList).setType(itemType)
            mBillingClient?.querySkuDetailsAsync(
                    params.build()) { responseCode, skuDetailsList ->
                listener.onSkuDetailsResponse(responseCode, skuDetailsList)
            }
        }

        executeServiceRequest(queryRequest)
    }

    private fun handlePurchase(purchase: Purchase) {
        Log.d(TAG, "Got a verified purchase: $purchase")

        mPurchases.add(purchase)
    }

    /**
     * Handle a result from querying of purchases and report an updated list to the listener
     */
    private fun onQueryPurchasesFinished(result: Purchase.PurchasesResult) {
        // Have we been disposed of in the meantime? If so, or bad result code, then quit
        if (mBillingClient == null || result.responseCode != OK) {
            Log.w(TAG, "Billing client was null or result code (" + result.responseCode
                    + ") was bad - quitting")
            return
        }

        Log.d(TAG, "Query inventory was successful.")

        // Update the UI and purchases inventory with new list of purchases
        mPurchases.clear()
        onPurchasesUpdated(result.billingResult, result.purchasesList)
    }

    /**
     * Checks if subscriptions are supported for current client
     *
     * Note: This method does not automatically retry for RESULT_SERVICE_DISCONNECTED.
     * It is only used in unit tests and after queryPurchases execution, which already has
     * a retry-mechanism implemented.
     *
     */
    private fun areSubscriptionsSupported(): Boolean {
        val billingResult = mBillingClient?.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
        if (billingResult?.responseCode != OK) {
            Log.w(TAG, "areSubscriptionsSupported() got an error response: ${billingResult?.responseCode}")
        }
        return billingResult?.responseCode == OK
    }

    /**
     * Query purchases across various use cases and deliver the result in a formalized way through
     * a listener
     */
    fun queryPurchases() {
        val queryToExecute = Runnable {
            val time = System.currentTimeMillis()
            val purchasesResult = mBillingClient?.queryPurchases(BillingClient.SkuType.INAPP)
            Log.i(TAG, "Querying purchases elapsed time: " + (System.currentTimeMillis() - time)
                    + "ms")
            // If there are subscriptions supported, we add subscription rows as well
            if (areSubscriptionsSupported()) {
                val subscriptionResult = mBillingClient?.queryPurchases(BillingClient.SkuType.SUBS)
                Log.i(TAG, "Querying purchases and subscriptions elapsed time: "
                        + (System.currentTimeMillis() - time) + "ms")

                if (subscriptionResult?.responseCode == OK) {
                    purchasesResult?.purchasesList?.addAll(
                            subscriptionResult.purchasesList)
                } else {
                    Log.e(TAG, "Got an error response trying to query subscription purchases")
                }
            } else if (purchasesResult?.responseCode == OK) {
                Log.i(TAG, "Skipped subscription purchases query since they are not supported")
            } else {
                Log.w(TAG, "queryPurchases() got an error response code: " + purchasesResult?.responseCode)
            }
            purchasesResult?.let {
                onQueryPurchasesFinished(purchasesResult)
            }
        }

        executeServiceRequest(queryToExecute)
    }

    fun startServiceConnection(executeOnSuccess: Runnable?) {
        mBillingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult?) {
                val billingResponseCode = billingResult?.responseCode

                billingResponseCode?.let {
                    Log.d(TAG, "Setup finished. Response code: $it")

                    if (it == OK) {
                        mIsServiceConnected = true
                        executeOnSuccess?.run()
                    }

                    billingClientResponseCode = it
                }
            }

            override fun onBillingServiceDisconnected() {
                mIsServiceConnected = false
            }
        })
    }

    private fun executeServiceRequest(runnable: Runnable) {
        if (mIsServiceConnected) {
            runnable.run()
        } else {
            // If billing service was disconnected, we try to reconnect 1 time.
            // (feel free to introduce your retry policy here).
            startServiceConnection(runnable)
        }
    }

    companion object {
        // Default value of mBillingClientResponseCode until BillingManager was not yeat initialized
        val BILLING_MANAGER_NOT_INITIALIZED = -1

        private val TAG = "BillingManager"
    }

}