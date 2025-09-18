package com.example.etrip1.model

import java.util.Date

// Pass.kt
/*
data class Pass(
    val passId: String = "",
    val userId: String = "",
    val type: String = "",
    val startDate: Date = Date(),
    val endDate: Date = Date(),
    val isActive: Boolean = true,
    val tripsRemaining: Int = 0
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Pass {
            return Pass(
                passId = map["passId"] as? String ?: "",
                userId = map["userid"] as? String ?: "",
                type = map["type"] as? String ?: "",
                startDate = (map["start_date"] as? com.google.firebase.Timestamp)?.toDate() ?: Date(),
                endDate = (map["end_date"] as? com.google.firebase.Timestamp)?.toDate() ?: Date(),
                isActive = map["is_active"] as? Boolean ?: true,
                tripsRemaining = map["tripsRemaining"] as? Int ?: 0
            )
        }
    }
}

 */