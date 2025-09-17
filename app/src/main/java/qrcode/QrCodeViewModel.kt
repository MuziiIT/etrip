package com.example.etrip1.qrcode

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

// Data class for Pass
data class Pass(
    val id: String,
    val type: String,
    val tripsRemaining: Int,
    val validUntil: Date
)

class QrCodeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _userPasses = MutableStateFlow<List<Pass>>(emptyList())
    val userPasses: StateFlow<List<Pass>> = _userPasses

    private val _selectedPass = MutableStateFlow<Pass?>(null)
    val selectedPass: StateFlow<Pass?> = _selectedPass

    private val _qrCodeBitmap = MutableStateFlow<Bitmap?>(null)
    val qrCodeBitmap: StateFlow<Bitmap?> = _qrCodeBitmap

    init {
        loadUserPasses()
    }

    private fun loadUserPasses() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("passes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("QR_VIEW_MODEL", "Error loading passes", error)
                    return@addSnapshotListener
                }

                val now = Date()
                val passes = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        // ✅ Read validUntil as Long millis
                        val validUntilMillis = doc.getLong("validUntil") ?: 0L
                        val validUntil = Date(validUntilMillis)

                        // Only include passes that are still valid
                        if (validUntil.after(now)) {
                            Pass(
                                id = doc.id,
                                type = doc.getString("type") ?: "",
                                tripsRemaining = doc.getLong("tripsRemaining")?.toInt() ?: 0,
                                validUntil = validUntil
                            )
                        } else null
                    } catch (e: Exception) {
                        Log.e("QR_VIEW_MODEL", "Error parsing pass", e)
                        null
                    }
                } ?: emptyList()

                _userPasses.value = passes

                // Auto-select the first available pass
                if (_selectedPass.value == null && passes.isNotEmpty()) {
                    selectPass(passes[0])
                }
            }
    }

    fun selectPass(pass: Pass) {
        _selectedPass.value = pass
        generateQrCode(pass)
    }

    private fun generateQrCode(pass: Pass) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch

                val qrData = """
                {
                    "userId": "$userId",
                    "passId": "${pass.id}",
                    "passType": "${pass.type}",
                    "tripsRemaining": ${pass.tripsRemaining},
                    "validUntil": "${pass.validUntil.time}",
                    "timestamp": "${System.currentTimeMillis()}"
                }
                """.trimIndent()

                val barcodeEncoder = BarcodeEncoder()
                val bitmap = barcodeEncoder.encodeBitmap(
                    qrData,
                    BarcodeFormat.QR_CODE,
                    400,
                    400
                )

                _qrCodeBitmap.value = bitmap
            } catch (e: Exception) {
                Log.e("QR_VIEW_MODEL", "Error generating QR code", e)
            }
        }
    }

    // ✅ Deduct trip from Firestore
    fun useTrip() {
        viewModelScope.launch {
            val currentPass = _selectedPass.value ?: return@launch
            val userId = auth.currentUser?.uid ?: return@launch

            if (currentPass.tripsRemaining > 0) {
                val newTripCount = currentPass.tripsRemaining - 1

                db.collection("users")
                    .document(userId)
                    .collection("passes")
                    .document(currentPass.id)
                    .update("tripsRemaining", newTripCount)
                    .addOnSuccessListener {
                        _selectedPass.value = currentPass.copy(tripsRemaining = newTripCount)
                        _userPasses.value = _userPasses.value.map { pass ->
                            if (pass.id == currentPass.id) {
                                pass.copy(tripsRemaining = newTripCount)
                            } else pass
                        }
                    }
            }
        }
    }
}
