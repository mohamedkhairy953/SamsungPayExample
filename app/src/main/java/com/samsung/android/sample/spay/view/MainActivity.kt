package com.samsung.android.sample.spay.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.samsung.android.sample.spay.R
import com.samsung.android.sdk.samsungpay.v2.PartnerInfo
import com.samsung.android.sdk.samsungpay.v2.SamsungPay
import com.samsung.android.sdk.samsungpay.v2.SpaySdk
import com.samsung.android.sdk.samsungpay.v2.StatusListener
import com.samsung.android.sdk.samsungpay.v2.payment.CardInfo
import com.samsung.android.sdk.samsungpay.v2.payment.PaymentInfo
import com.samsung.android.sdk.samsungpay.v2.payment.PaymentManager


class MainActivity : AppCompatActivity() {
    val samsungPayButton: ImageView by lazy { findViewById(R.id.samsung_pay_button) }
    private val partnerInfo by lazy {
        val bundle = Bundle()
        bundle.putString(
            SamsungPay.PARTNER_SERVICE_TYPE,
            getString(R.string.gradle_server_type)
        )
        PartnerInfo(getString(R.string.gradle_product_id), bundle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkSamsungPayStatus()
        val cardId = "cardID_test"

        val metaData = Bundle()
        metaData.putString(
            PaymentManager.EXTRA_PAY_OPERATION_TYPE,
            PaymentManager.PAY_OPERATION_TYPE_PAYMENT
        )
        metaData.putString(PaymentManager.EXTRA_ISSUER_NAME, "issuer name")
        metaData.putInt(
            PaymentManager.EXTRA_TRANSACTION_TYPE,
            PaymentManager.TRANSACTION_TYPE_MST or PaymentManager.TRANSACTION_TYPE_NFC
        )

        val cardInfo: CardInfo = CardInfo.Builder()
            .setBrand(SpaySdk.Brand.VISA)
            .setCardId(cardId)
            .setCardMetaData(metaData)
            .setBrand(SpaySdk.Brand.MASTERCARD)
            .build()

        samsungPayButton.setOnClickListener {
            PaymentManager(this, partnerInfo).startSimplePay(cardInfo,
                object : StatusListener {

                    override fun onSuccess(p0: Int, p1: Bundle?) {
                        Log.d("TAG", "onSuccess: $p1")
                        p1?.keySet()?.forEach {
                            Toast.makeText(
                                this@MainActivity,
                                "${p1.get(it)}   :: $it",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFail(p0: Int, p1: Bundle?) {
                        p1?.keySet()?.forEach {
                            Toast.makeText(
                                this@MainActivity,
                                "${p1.get(it)}   :: $it",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                })
        }
    }

    private fun checkSamsungPayStatus() {

        val samsungPay = SamsungPay(this, partnerInfo)
        try {
            samsungPay.getSamsungPayStatus(object : StatusListener {
                override fun onSuccess(status: Int, bundle: Bundle) {
                    when (status) {
                        SamsungPay.SPAY_NOT_SUPPORTED -> {
                            //  samsungPayButton.visibility = View.INVISIBLE
                            Toast.makeText(
                                this@MainActivity,
                                "SPAY_NOT_SUPPORTED",
                                Toast.LENGTH_SHORT
                            ).show()

                        }                            // Samsung Pay is not supported
                        SamsungPay.SPAY_NOT_READY -> {
                            Toast.makeText(this@MainActivity, "SPAY_NOT_READY", Toast.LENGTH_SHORT)
                                .show()
                            samsungPayButton.visibility = View.INVISIBLE

                        }                           // Activate Samsung Pay or update Samsung Pay, if needed
                        SamsungPay.SPAY_READY ->                             // Samsung Pay is ready
                        {
                            Toast.makeText(this@MainActivity, "SPAY_READY", Toast.LENGTH_SHORT)
                                .show()

                            samsungPayButton.visibility = View.VISIBLE
                        }
                        else ->                             // Not expected result
                        {
                            Toast.makeText(
                                this@MainActivity,
                                "Not expected result",
                                Toast.LENGTH_SHORT
                            ).show()
                            samsungPayButton.visibility = View.INVISIBLE
                        }
                    }
                }

                override fun onFail(errorCode: Int, bundle: Bundle) {
                    samsungPayButton.setVisibility(View.INVISIBLE)
                    Log.d("TAG", "checkSamsungPayStatus onFail() : $errorCode")
                }
            })
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

}