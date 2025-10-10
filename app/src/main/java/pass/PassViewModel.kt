package com.example.etrip1.pass

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class PassViewModel : ViewModel() {

    fun startPayment(
        context: Context,
        amount: Double,
        passType: String,
        onUrlReady: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            onError("User not logged in")
            return
        }

        val repository = PassRepository(context)
        viewModelScope.launch {
            repository.createPayment(amount, passType, userId, { paymentUrl ->
                // ✅ Only open payment URL now — don't assign yet
                onUrlReady(paymentUrl)
            }, onError)
        }
    }

    /** ✅ Call this ONLY after payment confirmation */
    fun assignPassAfterPayment(userId: String, passType: String) {
        assignPassToUser(userId, passType)
    }

    private fun assignPassToUser(userId: String, passType: String) {
        val db = FirebaseFirestore.getInstance()
        val passesCollection = db.collection("users").document(userId).collection("passes")

        val expiry = when (passType) {
            "daily" -> System.currentTimeMillis() + 24 * 60 * 60 * 1000
            "weekly" -> System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000
            "monthly" -> System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000
            else -> System.currentTimeMillis()
        }

        val trips = when (passType) {
            "daily" -> 3
            "weekly" -> 15
            "monthly" -> 60
            else -> 0
        }

        val newPass = hashMapOf(
            "type" to passType,
            "purchasedAt" to System.currentTimeMillis(),
            "validUntil" to expiry,
            "tripsRemaining" to trips
        )

        passesCollection.document("$passType pass")
            .set(newPass)
            .addOnSuccessListener {
                Log.d("PASS_UPDATE", "Pass assigned: $passType for user: $userId")

                // ✅ Log to history once (after confirmed payment)
                logPassPurchaseToHistory(userId, passType, trips)
            }
            .addOnFailureListener { e ->
                Log.e("PASS_UPDATE", "Error assigning pass", e)
            }
    }

    /** ✅ Log successful pass purchases to the user's history */
    private fun logPassPurchaseToHistory(userId: String, passType: String, trips: Int) {
        val db = FirebaseFirestore.getInstance()
        val historyEntry = hashMapOf(
            "type" to "purchase",
            "passType" to passType,
            "tripsAllocated" to trips,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("users")
            .document(userId)
            .collection("history")
            .add(historyEntry)
            .addOnSuccessListener {
                Log.d("PASS_HISTORY", "Purchase logged successfully for $userId")
            }
            .addOnFailureListener { e ->
                Log.e("PASS_HISTORY", "Failed to log purchase: ${e.message}")
            }
    }
}
