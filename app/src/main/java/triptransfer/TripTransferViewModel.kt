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

/**
 * ViewModel that:
 *  - listens to the current user's passes (availablePasses)
 *  - allows the current user to create a transfer request (createTransfer)
 *
 * Note: createTransfer writes to /users/{senderId}/transfers (allowed by your rules).
 * Do NOT write to recipient passes from the client unless your rules allow it.
 */
class TripTransferViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _availablePasses = MutableStateFlow<List<TravelPass>>(emptyList())
    val availablePasses: StateFlow<List<TravelPass>> = _availablePasses

    private val _transferStatus = MutableStateFlow<String?>(null)
    val transferStatus: StateFlow<String?> = _transferStatus

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting

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
                    // manual mapping to avoid timestamp/Date conversion surprises
                    val id = doc.id
                    val type = doc.getString("type") ?: ""
                    val trips = doc.getLong("tripsRemaining")?.toInt() ?: 0
                    val validUntil = doc.getLong("validUntil") ?: 0L
                    TravelPass(id = id, type = type, tripsRemaining = trips, validUntil = validUntil)
                } ?: emptyList()

                _availablePasses.value = passes
            }
    }

    /**
     * Create a transfer request under the sender's transfers subcollection.
     *
     * This write is permitted by your security rules (sender writes into their own transfers).
     * recipientId must be the recipient UID (not email) unless you implement an email->uid lookup that your rules permit.
     */
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

        // Create log of transfer
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
                // Execute the actual transfer immediately
                viewModelScope.launch {
                    try {
                        transferPass(recipientId, passId, trips)  // ✅ now calls the renamed function
                        transferRef.update("status", "completed")
                        _transferStatus.value = "Transfer successful!"
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

    /**
     * Optional helper: if you ever need an immediate client-side transfer (not recommended unless rules permit),
     * keep this but be aware it will attempt cross-user writes and will be blocked by your strict rules.
     *
     * I left it commented as a reference — prefer the createTransfer + acceptTransfer server-side flow.
     */
    @Suppress("unused")
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

                    // ✅ Deduct from sender
                    transaction.update(
                        senderPassRef,
                        "tripsRemaining",
                        currentTrips - tripsToTransfer
                    )

                    // ✅ Add to recipient, include senderId so rules allow it
                    transaction.set(
                        recipientPassRef,
                        mapOf(
                            "id" to passId,
                            "type" to type,
                            "tripsRemaining" to FieldValue.increment(tripsToTransfer.toLong()),
                            "senderId" to senderId, // 👈 crucial for rules
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

    }

