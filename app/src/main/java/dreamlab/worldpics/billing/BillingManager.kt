package dreamlab.worldpics.billing

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.*

class BillingManager(val activity: Activity) : PurchasesUpdatedListener {

    private lateinit var billingClient: BillingClient
    private lateinit var skuAdFreeBillingFlowParams: BillingFlowParams

    private val skuList = listOf(SKU_AD_FREE)

    companion object {
        const val SKU_AD_FREE = "ad_free"
    }

    init {
        setupBillingClient()
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(activity)
            .enablePendingPurchases()
            .setListener(this)
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d("BillingClient", "Setup Billing Done")
                    loadAllSKUs()
                    //queryPurchases()
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.d("BillingClient", "Failed")
            }
        })
    }

    private fun loadAllSKUs() {
        if (billingClient.isReady) {
            val params = SkuDetailsParams
                .newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP)
                .build()
            billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList.isNotEmpty()) {
                    for (skuDetails in skuDetailsList) {
                        if (skuDetails.sku == SKU_AD_FREE) {
                            skuAdFreeBillingFlowParams = BillingFlowParams
                                .newBuilder()
                                .setSkuDetails(skuDetails)
                                .build()

                        }
                    }
                }
            }
        } else {
            Log.d("BillingClient", "Billing Client not ready")
        }
    }

    fun launchBillingFlow() {
        billingClient.launchBillingFlow(activity, skuAdFreeBillingFlowParams)
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult?,
        purchases: MutableList<Purchase>?
    ) {
        purchases?.let {
            for (purchase in it) {
                if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                    acknowledgePurchase(purchase)
                } else if (billingResult?.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                    Toast.makeText(
                        activity,
                        "You have already donated, thank you!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Toast.makeText(activity, "Thanks for donating!", Toast.LENGTH_LONG).show()
                consumeAsync(purchase)
            }
        }
    }

    private fun consumeAsync(purchase: Purchase) {
        billingClient.consumeAsync(consumePurchaseParams(purchase)) { _, _ -> }
    }

    private fun consumePurchaseParams(purchase: Purchase): ConsumeParams {
        return ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .setDeveloperPayload(purchase.developerPayload)
            .build()
    }

    /**
     * Query purchases across various use cases and deliver the result in a formalized way through
     * a listener
     */
    private fun queryPurchases() {
        val time = System.currentTimeMillis()
        val purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
        Log.i(
            "BillingClient",
            "Querying purchases elapsed time: " + (System.currentTimeMillis() - time)
                    + "ms"
        )
        if (purchasesResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            Log.i(
                "BillingClient",
                "Skipped subscription purchases query since they are not supported"
            )
        } else {
            Log.w(
                "BillingClient",
                "queryPurchases() got an error response code: " + purchasesResult?.responseCode
            )
        }
        onQueryPurchasesFinished(purchasesResult)
    }

    /**
     * Handle a result from querying of purchases and report an updated list to the listener
     */
    private fun onQueryPurchasesFinished(result: Purchase.PurchasesResult) {
        result.purchasesList?.let {
            for (purchase in it) {
                consumeAsync(purchase)
            }
        }
    }
}