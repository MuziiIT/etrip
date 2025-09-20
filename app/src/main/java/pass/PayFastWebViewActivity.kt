package com.example.etrip1.pass

import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.etrip1.notification.NotificationService  // <--- matches package above

class PayFastWebViewActivity : AppCompatActivity() {

    private val passViewModel: PassViewModel by viewModels()
    private var notificationShown = false  //  Prevents duplicate notifications

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val webView = WebView(this)
        setContentView(webView)

        val passType = intent.getStringExtra("passType") ?: ""
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid

        webView.settings.javaScriptEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                Log.d("PAYFAST", "Redirected to: $url")

                when {
                    url.contains("success", ignoreCase = true) -> {
                        if (!notificationShown) {
                            notificationShown = true  //  Mark as handled

                            Toast.makeText(this@PayFastWebViewActivity, "Payment Successful!", Toast.LENGTH_LONG).show()

                            if (userId != null && passType.isNotEmpty()) {
                                passViewModel.assignPassAfterPayment(userId, passType)
                            }

                            NotificationService.showNotification(
                                this@PayFastWebViewActivity,
                                "Purchase Successful",
                                "Your $passType pass has been activated."
                            )
                            finish()
                        }
                        return true
                    }

                    url.contains("cancel", ignoreCase = true) -> {
                        if (!notificationShown) {
                            notificationShown = true  //  Mark as handled

                            Toast.makeText(this@PayFastWebViewActivity, "Payment Cancelled", Toast.LENGTH_LONG).show()

                            NotificationService.showNotification(
                                this@PayFastWebViewActivity,
                                "Purchase Cancelled",
                                "Your $passType payment was cancelled."
                            )
                            finish()
                        }
                        return true
                    }
                }

                return false
            }
        }

        val paymentUrl = intent.getStringExtra("url") ?: ""
        webView.loadUrl(paymentUrl)
    }
}

