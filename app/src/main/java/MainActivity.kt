package com.example.etrip1

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import auth.eTripNavHost
import com.example.etrip1.notification.EtripApp.Companion.TAG
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import ui.eTripTheme
import viewmodel.UserViewModel

class MainActivity : ComponentActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
        private const val DEFAULT_TOPIC = "etrip"
        private const val PREFS_NAME = "etrip_prefs"
        private const val KEY_FCM_TOKEN = "fcm_token"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // ✅ Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )
            }
        }

        // ✅ Fetch FCM token & subscribe to default topic
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                Toast.makeText(this, "FCM token fetch failed", Toast.LENGTH_SHORT).show()
                return@addOnCompleteListener
            }

            val token = task.result
            saveTokenToPrefs(token) // Save token locally
            Log.i(TAG, "FCM Token: $token")

            FirebaseMessaging.getInstance().subscribeToTopic(DEFAULT_TOPIC)
                .addOnCompleteListener { subTask ->
                    if (subTask.isSuccessful) {
                        Log.i(TAG, "Subscribed to topic: $DEFAULT_TOPIC")
                    } else {
                        Log.w(TAG, "Failed to subscribe to topic: $DEFAULT_TOPIC", subTask.exception)
                    }
                }
        }

        setContent {
            eTripTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val userViewModel: UserViewModel = viewModel()
                    eTripNavHost(userViewModel)
                }
            }
        }
    }

    // 🔹 Save FCM token to SharedPreferences
    private fun saveTokenToPrefs(token: String) {
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(KEY_FCM_TOKEN, token)
            apply()
        }
    }

    // 🔹 Retrieve FCM token
    private fun getTokenFromPrefs(): String? {
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(KEY_FCM_TOKEN, null)
    }
}
