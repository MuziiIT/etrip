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
        val userRef = db.collection("users").document(userId)

        // Run Firestore write async, not on UI thread
        val (expiry, trips) = when (passType) {
            "daily" -> Pair(System.currentTimeMillis() + 24 * 60 * 60 * 1000, 1)
            "weekly" -> Pair(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000, 7)
            "monthly" -> Pair(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000, 30)
            else -> Pair(System.currentTimeMillis(), 0)
        }

        val updates = mapOf(
            "current_Pass" to passType,
            "passExpiry" to expiry,
            "tripsLeft" to trips
        )

        userRef.update(updates)
            .addOnSuccessListener {
                Log.d("PASS_UPDATE", "Pass assigned successfully for $userId: $passType")
            }
            .addOnFailureListener { e ->
                Log.e("PASS_UPDATE", "Error assigning pass", e)
            }
    }

}