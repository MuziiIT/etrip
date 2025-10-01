package com.example.etrip1.pass

import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

class PayFastWebViewActivity : AppCompatActivity() {

    private val passViewModel: PassViewModel by viewModels()

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
                        Toast.makeText(this@PayFastWebViewActivity, "Payment Successful!", Toast.LENGTH_LONG).show()

                        // ✅ Update Firestore after success
                        if (userId != null && passType.isNotEmpty()) {
                            passViewModel.assignPassAfterPayment(userId, passType)
                        }

                        finish() // Close WebView
                        return true
                    }

                    url.contains("cancel", ignoreCase = true) -> {
                        Toast.makeText(this@PayFastWebViewActivity, "Payment Cancelled", Toast.LENGTH_LONG).show()
                        finish()
                        return true
                    }
                }

                return false // Allow WebView to load normally
            }
        }

        val paymentUrl = intent.getStringExtra("url") ?: ""
        webView.loadUrl(paymentUrl)
    }
}