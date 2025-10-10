package com.example.etrip1.triptransfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TripTransferViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _availablePasses = MutableStateFlow<List<TravelPass>>(emptyList())
    val availablePasses: StateFlow<List<TravelPass>> = _availablePasses

    private val _transferStatus = MutableStateFlow<String?>(null)
    val transferStatus: StateFlow<String?> = _transferStatus

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting

    private val _currentUserId = MutableStateFlow(auth.currentUser?.uid ?: "")
    val currentUserId: StateFlow<String> = _currentUserId

    init {
        loadUserPasses()
    }

    private fun loadUserPasses() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(userId)
            .collection("passes")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _transferStatus.value = "Error loading passes: ${e.message}"
                    return@addSnapshotListener
                }

                val passes = snapshot?.documents?.mapNotNull { doc ->
                    val id = doc.id
                    val type = doc.getString("type") ?: ""
                    val trips = doc.getLong("tripsRemaining")?.toInt() ?: 0
                    val validUntil = doc.getLong("validUntil") ?: 0L
                    TravelPass(id = id, type = type, tripsRemaining = trips, validUntil = validUntil)
                } ?: emptyList()

                _availablePasses.value = passes
            }
    }

    fun createTransfer(recipientId: String, passId: String, trips: Int) {
        val senderId = auth.currentUser?.uid ?: run {
            _transferStatus.value = "Not logged in"
            return
        }

        if (recipientId.isBlank() || passId.isBlank() || trips <= 0) {
            _transferStatus.value = "Invalid input"
            return
        }

        _isSubmitting.value = true
        _transferStatus.value = null

        val transferRef = firestore.collection("users")
            .document(senderId)
            .collection("transfers")
            .document()

        val transferData = mapOf(
            "senderId" to senderId,
            "recipientId" to recipientId,
            "passId" to passId,
            "trips" to trips,
            "status" to "pending",
            "timestamp" to System.currentTimeMillis()
        )

        transferRef.set(transferData)
            .addOnSuccessListener {
                viewModelScope.launch {
                    try {
                        transferPass(recipientId, passId, trips)
                        transferRef.update("status", "completed")
                        _transferStatus.value = "Transfer successful!"

                        // ✅ Log history for both sender & recipient
                        logTransferHistory(senderId, recipientId, passId, trips)

                    } catch (e: Exception) {
                        transferRef.update("status", "failed")
                        _transferStatus.value = "Transfer failed: ${e.message}"
                    }
                    _isSubmitting.value = false
                }
            }
            .addOnFailureListener { e ->
                _transferStatus.value = "Failed to create transfer: ${e.message}"
                _isSubmitting.value = false
            }
    }

    fun transferPass(
        recipientId: String,
        passId: String,
        tripsToTransfer: Int
    ) {
        viewModelScope.launch {
            try {
                val senderId = auth.currentUser?.uid ?: return@launch

                val senderPassRef = firestore.collection("users")
                    .document(senderId)
                    .collection("passes")
                    .document(passId)

                val recipientPassRef = firestore.collection("users")
                    .document(recipientId)
                    .collection("passes")
                    .document(passId)

                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(senderPassRef)
                    val currentTrips = snapshot.getLong("tripsRemaining")?.toInt() ?: 0
                    val type = snapshot.getString("type") ?: "Unknown"

                    if (currentTrips < tripsToTransfer) {
                        throw Exception("Not enough trips")
                    }

                    transaction.update(
                        senderPassRef,
                        "tripsRemaining",
                        currentTrips - tripsToTransfer
                    )

                    transaction.set(
                        recipientPassRef,
                        mapOf(
                            "id" to passId,
                            "type" to type,
                            "tripsRemaining" to FieldValue.increment(tripsToTransfer.toLong()),
                            "senderId" to senderId,
                            "timestamp" to System.currentTimeMillis()
                        ),
                        SetOptions.merge()
                    )
                }.await()

                _transferStatus.value = "Transfer successful!"
            } catch (e: Exception) {
                _transferStatus.value = "Transfer failed: ${e.message}"
            }
        }
    }

    /** ✅ Helper to log transfer events into users/{id}/history */
    private fun logTransferHistory(senderId: String, recipientId: String, passId: String, trips: Int) {
        val db = FirebaseFirestore.getInstance()
        val timestamp = System.currentTimeMillis()

        val senderHistory = mapOf(
            "type" to "transfer",
            "action" to "sent",
            "recipientId" to recipientId,
            "passId" to passId,
            "tripsTransferred" to trips,
            "timestamp" to timestamp
        )

        val recipientHistory = mapOf(
            "type" to "transfer",
            "action" to "received",
            "senderId" to senderId,
            "passId" to passId,
            "tripsTransferred" to trips,
            "timestamp" to timestamp
        )

        db.collection("users").document(senderId)
            .collection("history").add(senderHistory)

        db.collection("users").document(recipientId)
            .collection("history").add(recipientHistory)
    }
}
