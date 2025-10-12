package com.example.etrip1.schedules

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.etrip1.R
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ScheduleScreen(userName: String) {
    val context = LocalContext.current

    val schedules = listOf(
        BusSchedule("Bus 101", "Daantjie", "Nelspruit", listOf("Emkhatsini", "UMP"), "08:00", "09:30"),
        BusSchedule("Bus 202", "Nelspruit", "KaBhokweni", listOf("Sasol Garage"), "09:30", "10:45"),
        BusSchedule("Bus 303", "KaMagugu", "Nelspruit", listOf("Luhambo", "UMP"), "11:00", "12:30"),
        BusSchedule("Bus 404", "Nelspruit", "Lekazi", listOf("Station G", "Station H", "Station I"), "12:45", "14:00"),
        BusSchedule("Bus 505", "Nelspruit", "Matsulu", listOf("Riverside", "UMP"), "13:00", "14:30"),
        BusSchedule("Bus 606", "WhiteRiver", "Matafin", listOf("Town"), "14:15", "15:45"),
        BusSchedule("Bus 707", "Nelspruit", "Malelane Inkwazi", listOf("Riverside", "Government Complex"), "15:30", "16:45"),
        BusSchedule("Bus 808", "KaMagugu", "ILanga Mall", listOf("Tyger Wheels"), "16:00", "17:30"),
        BusSchedule("Bus 909", "Ngodini", "Nelspruit", listOf("Msholozi", "Riverside"), "17:00", "18:15"),
        BusSchedule("Bus 010", "Piennar", "KaBhokweni", listOf("Emkhatsini", "Riverside"), "18:30", "19:45")
    )

    var searchQuery by remember { mutableStateOf("") }
    val filteredSchedules = schedules.filter { schedule ->
        schedule.busNumber.contains(searchQuery, ignoreCase = true) ||
                schedule.departure.contains(searchQuery, ignoreCase = true) ||
                schedule.destination.contains(searchQuery, ignoreCase = true)
    }

    val scrollState = rememberScrollState()
    var currentTime by remember { mutableStateOf(getCurrentTime()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getCurrentTime()
            delay(1000L)
        }
    }

    // Define the color palette
    val primaryBlue = Color(0xFF1E88E5)      // Main Blue
    val accentOrange = Color(0xFFFF7043)    // Main Orange
    val cardBackground = Color(0xFFF0E5F5)  // Light lilac/purple for cards
    val darkText = Color(0xFF333333)

    // New gradient colors
    val lightBlue = Color(0xFFFF9043) // Light Blue mid-point

    // Define the Integrated Gradient Brush: Orange -> Light Blue -> Blue
    val integratedGradientBrush = Brush.verticalGradient(
        colors = listOf(
            accentOrange,    // Starts with Orange (0xFFFF7043)
            lightBlue,       // Transitions through Light Blue (0xFF81D4FA)
            primaryBlue      // Ends with Blue (0xFF1E88E5)
        ),
        // Extend the blend significantly down the screen
        endY = 2000f
    )

    val nextBus = filteredSchedules.minByOrNull { calculateTimeDifference(currentTime, it.departureTime) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(brush =integratedGradientBrush)


    ) {
        // ---------- Banner ----------
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.schedule),
                contentDescription = null, // decorative element
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp) // Height for the illustration
                    .align(Alignment.TopCenter),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.align(Alignment.Center).padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ---------- Today's Schedule Card ----------
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .shadow(10.dp, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1976D2))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Today's Schedule",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Text(getCurrentDate(), color = Color.White)
                        Text("Time: $currentTime", color = Color.White, fontWeight = FontWeight.Medium)
                    }
                }

                Text(
                    "Where to, $userName?",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.SemiBold)
                )

                // ---------- Search Field ----------
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Bus or Station") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White.copy(alpha = 0.7f),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = Color.White,
                        errorCursorColor = Color.Red,
                        selectionColors = TextSelectionColors(
                            handleColor = Color.White,
                            backgroundColor = Color.White.copy(alpha = 0.3f)
                        ),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                    )
                )

                if (nextBus != null) {
                    Text(
                        "Next Bus: ${nextBus.busNumber} • ${nextBus.departure} ➜ ${nextBus.destination} at ${nextBus.departureTime}",
                        color = Color.Yellow,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Text("No upcoming buses", color = Color.White.copy(alpha = 0.7f))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ---------- Available Buses ----------
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                "Available Buses",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D47A1)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            filteredSchedules.forEach { schedule ->
                BusScheduleCard(schedule, currentTime, schedule == nextBus)
                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ---------- Call Card ----------
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:080040063321")
                        }
                        context.startActivity(intent)
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Call, contentDescription = null, tint = Color(0xFF0D47A1))
                Spacer(Modifier.width(8.dp))
                Text("Need help? Call 0800-4006-3321", color = Color(0xFF0D47A1), fontWeight = FontWeight.Medium)
            }
        }
    }
}

// ---------- Bus Schedule Card ----------
@Composable
fun BusScheduleCard(schedule: BusSchedule, currentTime: String, isNextBus: Boolean) {
    val cardColor = if (isNextBus) Color(0xFFFF7043) else Color(0xFF1565C0) // Highlight next bus with orange, others dark blue
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(schedule.busNumber, color = Color.White, fontWeight = FontWeight.Bold)
                Text("Departs: ${schedule.departureTime}", color = Color.White)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = "From", tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("From: ${schedule.departure}", color = Color.White)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = "To", tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("To: ${schedule.destination}", color = Color.White)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Stops: ${schedule.stops.joinToString(", ")}", color = Color.White)
            Text("Arrival: ${schedule.arrivalTime}", color = Color.White)
            Text("Duration: ${calculateDuration(schedule.departureTime, schedule.arrivalTime)}", color = Color.White)
        }
    }
}

// ---------- Utilities ----------
fun getCurrentDate(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
fun getCurrentTime(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

fun calculateDuration(departureTime: String, arrivalTime: String): String {
    return try {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        val depart = format.parse(departureTime)
        val arrive = format.parse(arrivalTime)
        val diff = arrive.time - depart.time
        val hours = diff / (60 * 60 * 1000)
        val minutes = (diff % (60 * 60 * 1000)) / (60 * 1000)
        if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
    } catch (e: Exception) { "N/A" }
}

fun calculateTimeDifference(currentTime: String, departureTime: String): Long {
    return try {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        val current = format.parse(currentTime)
        val depart = format.parse(departureTime)
        val diff = depart.time - current.time
        if (diff >= 0) diff else Long.MAX_VALUE
    } catch (e: Exception) { Long.MAX_VALUE }
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Schedule Screen Preview"
)
fun ScheduleScreenPreview() {
    MaterialTheme {
        ScheduleScreen(userName = "Nelisa")
    }
}