package com.xsolla.android.payments.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.xsolla.android.payments.XPayments
import com.xsolla.android.payments.ui.utils.BrowserUtils

class ActivityPaystationBrowserProxy : ActivityPaystation() {

    companion object {
        fun checkAvailability(context: Context, url: String) =
            BrowserUtils.isBrowserAvailable(context, url)
                    || BrowserUtils.isCustomTabsAvailable(context, url)
    }

    private var needStartBrowser = false
    private lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent.getStringExtra(ARG_URL)
        if (url == null) {
            finish()
            return
        }
        if (savedInstanceState == null) {
            needStartBrowser = true
            this.url = url
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val uri = intent?.data
        if (uri == null) {
            finish()
            return
        }
        val invoiceId = uri.getQueryParameter("invoice_id")
        finishWithResult(
            Activity.RESULT_OK,
            XPayments.Result(XPayments.Status.COMPLETED, invoiceId)
        )
    }

    override fun onResume() {
        super.onResume()
        if (needStartBrowser) {
            if (BrowserUtils.isCustomTabsAvailable(this, url)) {
                BrowserUtils.launchCustomTab(this, url)
            } else {
                BrowserUtils.launchBrowser(this, url)
            }
            needStartBrowser = false
        } else {
            finishWithResult(
                Activity.RESULT_CANCELED,
                XPayments.Result(XPayments.Status.CANCELLED, null)
            )
        }
    }

    private fun finishWithResult(resultCode: Int, resultData: XPayments.Result) {
        val intent = Intent()
        intent.putExtra(RESULT, resultData)
        setResult(resultCode, intent)
        finish()
    }
}
