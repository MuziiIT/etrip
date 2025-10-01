package com.example.etrip1.notification

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class EtripApp : Application() {

    companion object {
        const val TAG = "EtripApp"
        private var instance: EtripApp? = null

        fun getInstance(): EtripApp? = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        try {
            // Initialize Firebase
            FirebaseApp.initializeApp(this)
            Log.d(TAG, "Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Firebase initialization failed", e)
        }
    }

    fun getSavedFcmToken(): String? {
        return try {
            val preferences = getSharedPreferences("fcm_prefs", MODE_PRIVATE)
            preferences.getString("fcm_token", null)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting FCM token", e)
            null
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        instance = null
    }
}