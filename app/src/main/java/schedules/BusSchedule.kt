package com.example.etrip1.schedules

data class BusSchedule(
    val busNumber: String,
    val departure: String,
    val destination: String,
    val stops: List<String>,
    val departureTime: String,
    val arrivalTime: String
)