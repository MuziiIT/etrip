package com.example.etrip1.pass

import android.content.Context
import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.nio.charset.Charset

class PassRepository(private val context: Context) {

    private val baseUrl = "https://etrip-backend.vercel.app" // ✅ Production backend

    fun createPayment(
        amount: Double,
        passType: String,
        userId: String, // pass real userId here
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val json = JSONObject().apply {
            put("amount", amount)
            put("itemName", "$passType Bus Pass")
            put("passType", passType)
            put("userId", userId) // ✅ no hardcode
        }

        val url = "$baseUrl/create_payment"

        val request = object : JsonObjectRequest(
            Method.POST, url, json,
            { response ->
                Log.d("PassRepository", "Payment URL received: $response")
                onSuccess(response.getString("paymentUrl"))
            },
            { error ->
                val networkResponse = error.networkResponse
                val errorMsg = if (networkResponse != null) {
                    val body = String(networkResponse.data, Charset.forName("UTF-8"))
                    "HTTP ${networkResponse.statusCode}: $body"
                } else {
                    error.toString()
                }
                Log.e("PassRepository", "Volley Error: $errorMsg")
                onError(errorMsg)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return hashMapOf("Content-Type" to "application/json")
            }
        }

        Volley.newRequestQueue(context).add(request)
    }


}
