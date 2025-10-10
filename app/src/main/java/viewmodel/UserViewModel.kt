package viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import model.User
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class UserViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _tripsRemaining = MutableStateFlow<Int?>(null)
    val tripsRemaining: StateFlow<Int?> get() = _tripsRemaining

    private val _activePassType = MutableStateFlow<String?>(null)
    val activePassType: StateFlow<String?> get() = _activePassType

    // ✅ New fields for expiry
    private val _passExpiry = MutableStateFlow<String?>(null)
    val passExpiry: StateFlow<String?> = _passExpiry

    private val _daysRemaining = MutableStateFlow<Int?>(null)
    val daysRemaining: StateFlow<Int?> = _daysRemaining

    init {
        if (auth.currentUser != null) {
            loadUserData()
            listenToActivePass()
        }
    }

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                loadUserData()
                listenToActivePass()
            } else {
                clearUser()
            }
        }
    }


    fun loadUserData() {
        val uid = auth.currentUser?.uid ?: return

        _loading.value = true
        db.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                _loading.value = false
                if (error != null) {
                    println("Error fetching user data: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val userData = snapshot.toObject(User::class.java)
                    _user.value = userData
                }
            }
    }

    private fun listenToActivePass() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .collection("passes")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    println("Error fetching passes: ${error.message}")
                    return@addSnapshotListener
                }

                var activeTrips: Int? = null
                var activeType: String? = null
                var latestExpiry: Long = 0

                val now = System.currentTimeMillis()

                if (snapshots != null && !snapshots.isEmpty) {
                    for (doc in snapshots.documents) {
                        val validUntil = doc.getLong("validUntil") ?: 0
                        val trips = doc.getLong("tripsRemaining")?.toInt() ?: 0
                        val type = doc.getString("type")

                        if (validUntil > now) {
                            if (validUntil > latestExpiry) {
                                latestExpiry = validUntil
                                activeTrips = trips
                                activeType = type
                            }
                        } else {
                            doc.reference.delete()
                                .addOnSuccessListener {
                                    println("Expired pass '${doc.id}' deleted for user $uid")
                                }
                                .addOnFailureListener { e ->
                                    println("Error deleting expired pass: ${e.message}")
                                }
                        }
                    }
                }

                _tripsRemaining.value = activeTrips
                _activePassType.value = activeType

                if (latestExpiry > 0) {
                    val expiryDate = Date(latestExpiry)
                    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    _passExpiry.value = formatter.format(expiryDate)

                    val diffMillis = latestExpiry - now
                    val diffDays = TimeUnit.MILLISECONDS.toDays(diffMillis).toInt()
                    _daysRemaining.value = diffDays
                } else {
                    _passExpiry.value = null
                    _daysRemaining.value = null
                }
            }
    }



    fun clearUser() {
        _user.value = null
        _tripsRemaining.value = null
        _activePassType.value = null
        _passExpiry.value = null
        _daysRemaining.value = null
    }
}
