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
                // ✅ Once we get the URL, continue as normal
                onUrlReady(paymentUrl)

                // 🔥 After payment starts, immediately assign pass (you may want to do this after verifying payment)
                assignPassToUser(userId, passType)
            }, onError)
        }
    }

    fun assignPassAfterPayment(userId: String, passType: String) {
        assignPassToUser(userId, passType)
    }


    fun assignPassToUser(userId: String, passType: String) {
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

        // Save under a fixed doc name like "daily pass", "weekly pass"
        passesCollection.document("$passType pass")
            .set(newPass)
            .addOnSuccessListener {
                Log.d("PASS_UPDATE", "Pass assigned: $passType for user: $userId")
            }
            .addOnFailureListener { e ->
                Log.e("PASS_UPDATE", "Error assigning pass", e)
            }
    }



}