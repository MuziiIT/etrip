package com.example.etrip1.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHistoryScreen(viewModel: UserHistoryViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        viewModel.fetchUserHistory()
    }

    val history by viewModel.history.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My History") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1976D2), titleContentColor = Color.White),
                navigationIcon = {
                    Icon(
                        Icons.Default.History,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF1565C0))
                        Spacer(Modifier.height(8.dp))
                        Text("Loading your history...")
                    }
                }

                history.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No history yet", style = MaterialTheme.typography.bodyLarge)
                    }
                }

                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(history) { item ->
                            HistoryCard(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryCard(item: UserHistoryItem) {
    val icon = when (item.type) {
        "purchase" -> Icons.Default.ShoppingCart
        "transfer" -> Icons.Default.SwapHoriz
        "ride" -> Icons.Default.DirectionsBus
        else -> Icons.Default.History
    }

    val color = when (item.type) {
        "purchase" -> Color(0xFFD1C4E9)
        "transfer" -> Color(0xFFFFECB3)
        "ride" -> Color(0xFFC8E6C9)
        else -> Color(0xFFEEEEEE)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color(0xFF424242))
                Spacer(Modifier.width(8.dp))
                Text(item.type.replaceFirstChar { it.uppercase() }, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(8.dp))

            item.details.forEach { (key, value) ->
                Text("$key: $value", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(6.dp))
            Text(
                "Date: ${item.timestamp.toFormattedDate()}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )
        }
    }
}

private fun Long.toFormattedDate(): String =
    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(this))

@Preview(showBackground = true)
@Composable
fun PreviewUserHistory() {
    val sample = UserHistoryItem(
        type = "ride",
        timestamp = System.currentTimeMillis(),
        details = mapOf("busId" to "BUS123", "routeId" to "R45", "tripsRemaining" to 12)
    )
    HistoryCard(sample)
}
