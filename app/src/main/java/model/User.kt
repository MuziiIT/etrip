package model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class User(
    val userid: String = "",
    val name: String = "",
    val email: String = "",
    var phone: String = "",
    var current_Pass: String = " ",
    var tripsLeft: Int = 0,
    val role: String = "user", // user or driver

    @get:PropertyName("created_at")
    @set:PropertyName("created_at")
    var createdAt: Timestamp? = null
)


data class Trip(
    val tripId: String = "",
    val route: String = "",
    val date: String = "", // ISO format
    val fare: Double = 0.0,
    val status: String = "" // "completed", "upcoming", "cancelled"
)