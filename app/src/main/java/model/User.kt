package model

data class User(
    val userid: String = "",
    val name: String = "",
    val email: String = "",
    var phone: String = "",
    var current_Pass: String = " ",
    var tripsLeft: Int = 0
)
