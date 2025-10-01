package com.example.etrip1.triptransfer

/**
 * Firestore-friendly model for a user pass.
 * validUntil stored as epoch millis (Long) per your preference.
 */
data class TravelPass(
    val id: String = "",
    val type: String = "",
    val tripsRemaining: Int = 0,
    val validUntil: Long = 0L
)
