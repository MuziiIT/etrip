package com.example.etrip1.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import viewmodel.UserViewModel
import java.util.concurrent.TimeUnit

object AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    val currentUser get() = auth.currentUser
    val isUserLoggedIn get() = currentUser != null

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(task.exception?.message ?: "Login failed")
                }
            }
    }

    fun register(
        email: String,
        password: String,
        fullName: String,
        phone: String = "",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = currentUser
                    firebaseUser?.let { user ->
                        // Update Auth profile
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(fullName)
                            .build()

                        user.updateProfile(profileUpdates)

                        // Save user details in Firestore (users collection)
                        val userData = hashMapOf(
                            "userid" to user.uid,
                            "name" to fullName,
                            "email" to email,
                            "phone" to phone
                        )

                        db.collection("users").document(user.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                onError(e.message ?: "Failed to save user in Firestore")
                            }
                    }
                } else {
                    onError(task.exception?.message ?: "Registration failed")
                }
            }
    }

    fun logout(userViewModel: UserViewModel, onLoggedOut: () -> Unit) {
        auth.signOut()                // Firebase sign out
        userViewModel.clearUser()     // Clear ViewModel user data
        onLoggedOut()                 // Navigate back to login
    }

    fun resetPassword(
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(task.exception?.message ?: "Password reset failed")
                }
            }
    }

    // Add a new pass for the user after purchase
    fun addUserPass(
        passType: String,
        trips: Int,
        validityDays: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val currentUser = currentUser
        if (currentUser != null) {
            val validUntil = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(validityDays.toLong())

            val passData = hashMapOf(
                "type" to passType,
                "tripsRemaining" to trips,
                "validUntil" to validUntil,
                "purchasedAt" to System.currentTimeMillis(),
                "validityDays" to validityDays
            )

            // Add to user's passes subcollection
            db.collection("users").document(currentUser.uid)
                .collection("passes")
                .add(passData)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onError(e.message ?: "Failed to add pass")
                }
        } else {
            onError("User not logged in")
        }
    }

    // Update trips remaining for a specific pass
    fun updatePassTrips(
        passId: String,
        newTripsCount: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val currentUser = currentUser
        if (currentUser != null) {
            val updates = hashMapOf<String, Any>(
                "tripsRemaining" to newTripsCount
            )

            db.collection("users").document(currentUser.uid)
                .collection("passes")
                .document(passId)
                .update(updates)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onError(e.message ?: "Failed to update trips") }
        } else {
            onError("User not logged in")
        }
    }

    // Delete expired pass
    fun deletePass(
        passId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val currentUser = currentUser
        if (currentUser != null) {
            db.collection("users").document(currentUser.uid)
                .collection("passes")
                .document(passId)
                .delete()
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onError(e.message ?: "Failed to delete pass") }
        } else {
            onError("User not logged in")
        }
    }
}