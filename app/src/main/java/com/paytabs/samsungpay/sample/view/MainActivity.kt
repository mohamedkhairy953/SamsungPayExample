package com.paytabs.samsungpay.sample.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.paytabs.samsungpay.sample.R
import com.samsung.android.sdk.samsungpay.BuildConfig
import com.samsung.android.sdk.samsungpay.v2.PartnerInfo
import com.samsung.android.sdk.samsungpay.v2.SamsungPay
import com.samsung.android.sdk.samsungpay.v2.SpaySdk
import com.samsung.android.sdk.samsungpay.v2.payment.CardInfo
import com.samsung.android.sdk.samsungpay.v2.payment.CustomSheetPaymentInfo
import com.samsung.android.sdk.samsungpay.v2.payment.PaymentManager
import com.samsung.android.sdk.samsungpay.v2.payment.PaymentManager.CustomSheetTransactionInfoListener
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.AmountBoxControl
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.AmountConstants
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.CustomSheet
import java.util.*


class MainActivity : AppCompatActivity() ,CustomSheetTransactionInfoListener{
    private val TAG = "SampleMerchantActivity"
    private val AMOUNT_CONTROL_ID = "amount_control_id"
    private var paymentManager: PaymentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val brandList = ArrayList<SpaySdk.Brand>()
        brandList.add(SpaySdk.Brand.VISA)
        brandList.add(SpaySdk.Brand.MASTERCARD)
        val customSheet = CustomSheet()
        customSheet.addControl(makeAmountControl())
        val customSheetPaymentInfo = CustomSheetPaymentInfo.Builder()
            .setMerchantId("123456")
            .setMerchantName("Sample Merchant")
            .setOrderNumber("12345566")
            .setAllowedCardBrands(brandList)
            .setCardHolderNameEnabled(true)
            .setRecurringEnabled(false)
            .setCustomSheet(customSheet)
            .build()
        try {
            val bundle = Bundle()
            bundle.putString(
                SamsungPay.PARTNER_SERVICE_TYPE,
                SpaySdk.ServiceType.INAPP_PAYMENT.toString()
            )
            val partnerInfo = PartnerInfo(getString(R.string.gradle_product_id), bundle)
            paymentManager = PaymentManager(this, partnerInfo)
            paymentManager!!.startInAppPayWithCustomSheet(customSheetPaymentInfo, this)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            Toast.makeText(this@MainActivity, "Fail ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            Log.e(TAG, e.toString())
        } catch (e: NullPointerException) {
            e.printStackTrace()
            Toast.makeText(this@MainActivity, "Fail ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            Log.e(TAG, e.toString())
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            Toast.makeText(this@MainActivity, "Fail  ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            Log.e(TAG, e.toString())
        }
    }

    override fun onCardInfoUpdated(cardInfo: CardInfo?, customSheet: CustomSheet) {
        val amountBoxControl = customSheet.getSheetControl(AMOUNT_CONTROL_ID) as AmountBoxControl
        amountBoxControl.setAmountTotal(1.0, AmountConstants.FORMAT_TOTAL_PRICE_ONLY) // grand total
        customSheet.updateControl(amountBoxControl)
        // Call updateSheet() with AmountBoxControl; mandatory.
        try {
            paymentManager!!.updateSheet(customSheet)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    override fun onSuccess(
        customSheetPaymentInfo: CustomSheetPaymentInfo?,
        s: String?,
        bundle: Bundle?
    ) {
        Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_LONG).show()
        Log.d(TAG, s!!)
    }

    override fun onFailure(i: Int, bundle: Bundle) {
        Toast.makeText(this@MainActivity, "Fail $i", Toast.LENGTH_LONG).show()
        Log.e(TAG, "$i ")
        for (key in bundle.keySet()) {
            Log.d(TAG, key + " : " + bundle[key])
            Toast.makeText(this@MainActivity, key + " : " + bundle[key], Toast.LENGTH_LONG).show()
        }
    }

    private fun makeAmountControl(): AmountBoxControl? {
        val amountBoxControl = AmountBoxControl(AMOUNT_CONTROL_ID, "AED")
        amountBoxControl.setAmountTotal(1.0, AmountConstants.FORMAT_TOTAL_PRICE_ONLY)
        return amountBoxControl
    }
}