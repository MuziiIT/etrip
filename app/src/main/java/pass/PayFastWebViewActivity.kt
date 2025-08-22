package com.example.etrip1.pass

import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class PayFastWebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val webView = WebView(this)
        setContentView(webView)

        webView.settings.javaScriptEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()

                // ✅ Detect success/cancel (these must match your PayFast RETURN_URL + CANCEL_URL)
                if (url.contains("success")) {
                    finish() // Close WebView after success
                } else if (url.contains("cancel")) {
                    finish()
                }

                return false // Load inside WebView
            }
        }

        val paymentUrl = intent.getStringExtra("url") ?: ""
        webView.loadUrl(paymentUrl)
    }
}
