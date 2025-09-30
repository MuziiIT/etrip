package com.example.etrip1.schedules





import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

// ------------------- Schedule Screen -------------------
import androidx.compose.foundation.border

import androidx.compose.runtime.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush


import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.res.painterResource
import com.example.etrip1.R


@Composable
fun ScheduleScreen() {
    val schedules = listOf(
        BusSchedule(
            "Bus 101",
            "Daantjie",
            "Nelspruit",
            listOf("Emkhatsini", "UMP"),
            "08:00",
            "09:30"
        ),
        BusSchedule("Bus 202", "Nelspruit", "KaBhokweni", listOf("Sasol Garadge"), "09:30", "10:45"),
        BusSchedule("Bus 303", "KaMagugu", "Nelspruit", listOf("Luhambo", "UMP"), "11:00", "12:30"),
        BusSchedule("Bus 404", "Nelspruit", "Lekazi", listOf("Station G", "Station H", "Station I"), "12:45", "14:00"),
        BusSchedule("Bus 505", "Nelspruit", "Matsulu", listOf("Riverside", "UMP"), "13:00", "14:30"),
        BusSchedule("Bus 606", "WhiteRiver", "Matafin", listOf("Town"), "14:15", "15:45"),
        BusSchedule("Bus 707", "Nelspruit", "Malelane Inkwazi", listOf("Riverside", "Government complex"), "15:30", "16:45"),
        BusSchedule("Bus 808", "KaMagugu", "ILanga Mall", listOf("Tyger Wheels"), "16:00", "17:30"),
        BusSchedule("Bus 909", "Ngodini", "Nelspruit", listOf("Msholozi", "Riverside"), "17:00", "18:15"),
        BusSchedule("Bus 010", "Piennar", "KaBhokweni", listOf("Emkhatsini", "Riverside"), "18:30", "19:45")
    )

    var searchQuery by remember { mutableStateOf("") }

    // Filtered schedules based on search query
    val filteredSchedules = schedules.filter { schedule ->
        schedule.busNumber.contains(searchQuery, ignoreCase = true) ||
                schedule.departure.contains(searchQuery, ignoreCase = true) ||
                schedule.destination.contains(searchQuery, ignoreCase = true)
    }

    // Scroll state for vertical scrolling
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState), // Make entire column scrollable
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                painter = painterResource(id = R.drawable.logoo),
                contentDescription = "eTrip Logo",
                modifier = Modifier
                    .size(60.dp) // adjust size as needed
            )
        }


        // Date header
        Text(
            text = "Schedules for ${getCurrentDate()}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFA726), shape = RoundedCornerShape(8.dp))
                .padding(8.dp),
            color = Color.White
        )

        // Search field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Bus or Station") },
            modifier = Modifier.fillMaxWidth()
        )

        // Next Bus Highlight
        if (filteredSchedules.isNotEmpty()) {
            val nextBus = filteredSchedules.first()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(12.dp))
                    .border(
                        width = 2.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.Yellow, Color(0xFFFFA726), Color.Yellow)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1976D2)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    text = "Next Bus leaves at ${nextBus.departureTime} from ${nextBus.departure}",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White),
                    modifier = Modifier.padding(12.dp)
                )
            }
        } else {
            Text(
                text = "No buses match your search",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                modifier = Modifier.padding(12.dp)
            )
        }

        // All filtered bus schedules
        filteredSchedules.forEach { schedule ->
            BusScheduleCard(schedule)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Footer call-to-action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Call, contentDescription = null, tint = Color(0xFF1976D2))
            Spacer(Modifier.width(8.dp))
            Text("Need help? Call 0800-4006-3321", color = Color(0xFF1976D2))
        }
    }
}




// ------------------- Bus Schedule Card -------------------
@Composable
fun BusScheduleCard(schedule: BusSchedule) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFA726)),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Bus number + departure time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    schedule.busNumber,
                    style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
                )

                Text(
                    schedule.departureTime,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // From
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = "Departure", tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("From: ${schedule.departure}", color = Color.White)
            }

            // To
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = "Destination", tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("To: ${schedule.destination}", color = Color.White)
            }

            // Stops
            Text("Stops: ${schedule.stops.joinToString(", ")}", color = Color.White)

            // Arrival + Duration
            Text("Arrival: ${schedule.arrivalTime}", color = Color.White)
            Text("Duration: ${calculateDuration(schedule.departureTime, schedule.arrivalTime)}", color = Color.White)
        }
    }
}

// ------------------- Helper Functions -------------------
fun getCurrentDate(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

fun calculateDuration(departureTime: String, arrivalTime: String): String {
    return try {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        val depart = format.parse(departureTime)
        val arrive = format.parse(arrivalTime)
        val diff = arrive.time - depart.time
        val hours = diff / (60 * 60 * 1000)
        val minutes = (diff % (60 * 60 * 1000)) / (60 * 1000)
        if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
    } catch (e: Exception) {
        "N/A"
    }
}
