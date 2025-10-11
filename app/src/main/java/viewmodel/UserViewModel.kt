package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import model.User

class UserViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    // ✅ Step 1: init block to auto-load user data
    init {
        if (auth.currentUser != null) {
            loadUserData()
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

    fun clearUser() {
        _user.value = null
    }
}
