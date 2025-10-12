package com.example.etrip1.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.etrip1.R
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHistoryScreen(viewModel: UserHistoryViewModel = viewModel()) {
    LaunchedEffect(Unit) { viewModel.fetchUserHistory() }

    val history by viewModel.history.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Gradient background: Orange -> Light Blue -> Blue
    val integratedGradientBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFFF7043), Color(0xFFFF9043), Color(0xFF1E88E5)),
        endY = 2000f
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(integratedGradientBrush)
    ) {
        // ---------- Banner ----------
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.history),
                contentDescription = "History Banner",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ---------- CONTENT ----------
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Loading your history...", color = Color.White)
                    }
                }
            }

            history.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No history yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(history) { item ->
                        HistoryCard(item)
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
        "ride" -> Icons.Default.Notifications
        else -> Icons.Default.Star
    }

    // Orange background for all history cards
    val backgroundColor = Color(0xFF2196F3)

    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    item.type.replaceFirstChar { it.uppercase() },
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(8.dp))

            item.details.forEach { (key, value) ->
                Text(
                    "$key: $value",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(6.dp))
            Text(
                "Date: ${item.timestamp.toFormattedDate()}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

private fun Long.toFormattedDate(): String =
    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(this))

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "User History Screen Preview"
)
@Composable
fun UserHistoryScreenPreview() {
    MaterialTheme {
        UserHistoryScreen()
    }
}
