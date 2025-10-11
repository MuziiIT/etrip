
package com.example.etrip1.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class UserHistoryItem(
    val type: String = "", // "purchase", "transfer", "ride"
    val timestamp: Long = 0L,
    val details: Map<String, Any?> = emptyMap()
)

class UserHistoryViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _history = MutableStateFlow<List<UserHistoryItem>>(emptyList())
    val history: StateFlow<List<UserHistoryItem>> = _history

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchUserHistory() {
        val userId = auth.currentUser?.uid ?: return
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val historyRef = db.collection("users")
                    .document(userId)
                    .collection("history")
                    .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .await()

                val items = historyRef.documents.mapNotNull { doc ->
                    val type = doc.getString("type") ?: return@mapNotNull null
                    val timestamp = doc.getLong("timestamp") ?: 0L
                    val details = doc.data?.toMutableMap() ?: mutableMapOf()
                    details.remove("type") // remove redundant key
                    details.remove("timestamp")

                    UserHistoryItem(
                        type = type,
                        timestamp = timestamp,
                        details = details
                    )
                }

                _history.value = items
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
