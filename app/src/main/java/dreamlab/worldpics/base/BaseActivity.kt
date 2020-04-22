package dreamlab.worldpics.base

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.widget.Toast
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.codemybrainsout.ratingdialog.RatingDialog
import com.github.javiersantos.appupdater.AppUpdater
import com.github.javiersantos.appupdater.AppUpdaterUtils
import com.github.javiersantos.appupdater.enums.AppUpdaterError
import com.github.javiersantos.appupdater.enums.Display
import com.github.javiersantos.appupdater.enums.UpdateFrom
import com.github.javiersantos.appupdater.objects.Update
import com.google.ads.consent.*
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.FirebaseDatabase
import dagger.android.support.DaggerAppCompatActivity
import dreamlab.worldpics.BuildConfig
import dreamlab.worldpics.WorldPics
import dreamlab.worldpics.billing.BillingManager
import dreamlab.worldpics.model.Feedback
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseActivity : DaggerAppCompatActivity(), BaseViewFragmentHelper {

    private var billingManager: BillingManager? = null
    private var form: ConsentForm? = null

    override val mFragmentManager: androidx.fragment.app.FragmentManager
        get() = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        initAppUpdater()
        initRatingBar()
        initBillingManager()

        if (!WorldPics.isPremium) {
            MobileAds.initialize(this, BuildConfig.admob_app_id)
            checkForConsent()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun replacePushFragment(idFrameContainer: Int, baseFragment: BaseFragment<*>, withTag: String, anim: Anim, backStack: String?) {
        super.replacePushFragment(idFrameContainer, baseFragment, withTag, anim, backStack)
    }

    override fun replacePopFragment(idFrameContainer: Int, baseFragment: BaseFragment<*>, withTag: String, anim: Anim) {
        super.replacePopFragment(idFrameContainer, baseFragment, withTag, anim)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun checkForConsent() {
        val consentInformation = ConsentInformation.getInstance(this)
        val publisherIds = arrayOf("pub-1216156475155252")
        consentInformation.requestConsentInfoUpdate(publisherIds, object : ConsentInfoUpdateListener {
            override fun onConsentInfoUpdated(consentStatus: ConsentStatus) {
                when (consentStatus) {
                    ConsentStatus.PERSONALIZED -> ConsentInformation.getInstance(this@BaseActivity).consentStatus = ConsentStatus.PERSONALIZED
                    ConsentStatus.NON_PERSONALIZED -> ConsentInformation.getInstance(this@BaseActivity).consentStatus = ConsentStatus.NON_PERSONALIZED
                    ConsentStatus.UNKNOWN -> if (consentInformation.isRequestLocationInEeaOrUnknown) {
                        requestConsent()
                    }
                }
            }

            override fun onFailedToUpdateConsentInfo(errorDescription: String) {
                // User's consent status failed to update.
            }
        })
    }

    private fun initBillingManager() {
        // Create and initialize BillingManager which talks to BillingLibrary
        billingManager = BillingManager(this, object : BillingManager.BillingUpdatesListener {
            override fun onBillingClientSetupFinished() {

            }

            override fun onConsumeFinished(token: String, result: Int) {

            }

            override fun onPurchasesUpdated(purchases: List<Purchase>) {
                for (purchase in purchases) {
                    when (purchase.sku) {
                        "ad_free" -> {
                            WorldPics.isPremium = true
                        }
                    }
                }
            }
        })
    }

    private fun initRatingBar() {
        RatingDialog.Builder(this)
                .threshold(3f)
                .playstoreUrl("https://play.google.com/store/apps/details?id=dreamlab.worldpics&hl=en_US")
                .session(7)
                .onRatingBarFormSumbit { feedback ->
                    val df = SimpleDateFormat("dd MM yyyy HH:mm:ss")
                    val today = Calendar.getInstance().time
                    val reportDate = df.format(today)

                    val database = FirebaseDatabase.getInstance()
                    val reference = database.getReference("feedback")

                    reference.child(reportDate).setValue(
                            Feedback(feedback, BuildConfig.VERSION_NAME, Build.VERSION.SDK_INT)
                    )

                    Toast.makeText(this, "Thank you!", Toast.LENGTH_SHORT).show()
                }.build().show()
    }

    private fun initAppUpdater() {
        val appUpdaterUtils = AppUpdaterUtils(this)
        appUpdaterUtils.withListener(object : AppUpdaterUtils.UpdateListener {
            override fun onSuccess(update: Update, isUpdateAvailable: Boolean?) {
                isUpdateAvailable?.let {
                    val latestVersion = update.latestVersion
                    val appUpdater = AppUpdater(this@BaseActivity)
                            .setDisplay(Display.DIALOG)
                            .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                            .setCancelable(false)
                            .setButtonDoNotShowAgain("")
                            .setTitleOnUpdateAvailable("New update available!")
                            .setContentOnUpdateAvailable("Update " + latestVersion + " is available to download! " +
                                    "Downloading the latest update you will get new features, improvements and bug fixes of World Pics.")
                            .setButtonUpdate("Update")
                            .setButtonUpdateClickListener { _, _ ->
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=dreamlab.worldpics&hl=en_US"))
                                startActivity(intent)
                            }
                            .setButtonDismiss("Cancel")
                            .showEvery(2)
                    appUpdater.start()
                }
            }

            override fun onFailed(appUpdaterError: AppUpdaterError) {

            }
        })
        appUpdaterUtils.start()
    }

    fun requestConsent() {
        val privacyUrl = URL(WorldPics.PRIVACY_POLICY_URL)
        form = ConsentForm.Builder(this, privacyUrl)
                .withListener(object : ConsentFormListener() {
                    override fun onConsentFormLoaded() {
                        if (!isFinishing) {
                            form?.show()
                        }
                    }

                    override fun onConsentFormOpened() {

                    }

                    override fun onConsentFormClosed(consentStatus: ConsentStatus?, userPrefersAdFree: Boolean?) {
                        userPrefersAdFree?.let {
                            if (it) {
                                billingManager?.initiatePurchaseFlow("ad_free", BillingClient.SkuType.INAPP)
                            }
                        }
                    }

                    override fun onConsentFormError(errorDescription: String?) {

                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .withAdFreeOption()
                .build()

        form?.load()
    }
}
