package com.example.etrip1.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.etrip1.R
import com.example.etrip1.MainActivity   // ✅ Corrected MainActivity import
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Extract title and body from notification or data payload
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "Etrip"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: "You have a new message"

        // When a push notification arrives → show local notification
        NotificationService.showNotification(
            this,
            title,
            body
        )

        val channelId = "etrip_default_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Etrip Notifications",
                NotificationManager.IMPORTANCE_HIGH // high importance for heads-up notifications
            ).apply {
                description = "Channel for Etrip app notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Prepare intent to open MainActivity when notification is clicked
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build the notification
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notification) // ensure this icon exists
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        // Use safe notification ID to avoid integer overflow
        val notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())

        // Optional: log data payload if exists
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Data Payload: ${remoteMessage.data}")
        }
    }

    /**
     * Called when the FCM registration token is refreshed.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        saveTokenLocally(token)
    }

    private fun saveTokenLocally(token: String) {
        val sharedPref = getSharedPreferences("etrip_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("fcm_token", token)
            apply()
        }
    }

    companion object {
        private const val TAG = "MyFirebaseMessaging"

        /**
         * Helper method to fetch FCM token manually
         */
        fun fetchToken(context: Context) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d(TAG, "Fetched FCM Token: $token")

                    // Save token locally
                    val sharedPref = context.getSharedPreferences("etrip_prefs", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("fcm_token", token)
                        apply()
                    }

                    // Subscribe device to a topic (example: "etrip")
                    FirebaseMessaging.getInstance().subscribeToTopic("etrip")
                        .addOnCompleteListener { subTask ->
                            if (subTask.isSuccessful) {
                                Log.d(TAG, "Subscribed to topic: etrip")
                            } else {
                                Log.w(TAG, "Topic subscription failed", subTask.exception)
                            }
                        }

                } else {
                    Log.w(TAG, "Fetching FCM token failed", task.exception)
                }
            }
        }
    }
}
