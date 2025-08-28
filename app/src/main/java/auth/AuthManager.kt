package auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import viewmodel.UserViewModel


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

                        // Save user details in Firestore
                        val userData = hashMapOf(
                            "userid" to user.uid,
                            "name" to fullName,
                            "email" to email,
                            "phone" to phone,

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

    // AuthManager.kt
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
}
