package triptransfer



import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import kotlinx.coroutines.launch



data class TravelPass(val name: String, val trips: Int)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripTransferTheme {
                PassManagerScreen()
            }
        }
    }
}

@Composable
fun PassManagerScreen() {
    val userPasses = remember {
        mutableStateListOf(
            TravelPass("Daily Pass", 3),
            TravelPass("Weekly Pass", 15),
            TravelPass("Monthly Pass", 60)
        )
    }
    var transferStatus by remember { mutableStateOf<String?>(null) }

    fun showNotification(message: String) {
        transferStatus = message
    }

    TransferPassScreen(
        availablePasses = userPasses,
        onTransfer = { _, selectedPass ->
            userPasses.remove(selectedPass)
            val remainingList = if (userPasses.isEmpty()) {
                "No passes left"
            } else {
                userPasses.joinToString { "${it.name} (${it.trips} trips)" }
            }
            showNotification("Transfer successful! Remaining passes: $remainingList")
            true
        },
        transferStatus = transferStatus,
        clearStatus = { transferStatus = null }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferPassScreen(
    availablePasses: List<TravelPass>,
    onTransfer: suspend (recipientId: String, pass: TravelPass) -> Boolean,
    transferStatus: String?,
    clearStatus: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedPass by remember { mutableStateOf<TravelPass?>(null) }
    var transferDropdownExpanded by remember { mutableStateOf(false) }
    var recipient by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Available Passes Section
        Text(
            text = "Available Passes",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (availablePasses.isEmpty()) {
            Text(
                "No passes available",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(availablePasses) { pass ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF9800)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = pass.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "${pass.trips} trips",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Transfer Section
        Text(
            text = "Transfer Pass",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Select Pass Dropdown
        ExposedDropdownMenuBox(
            expanded = transferDropdownExpanded,
            onExpandedChange = { transferDropdownExpanded = !transferDropdownExpanded }
        ) {
            TextField(
                value = selectedPass?.name ?: "Select a pass to transfer",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = transferDropdownExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                label = { Text("Choose Pass", fontWeight = FontWeight.Bold, color = Color.Black) }
            )
            ExposedDropdownMenu(
                expanded = transferDropdownExpanded,
                onDismissRequest = { transferDropdownExpanded = false }
            ) {
                availablePasses.forEach { pass ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                "${pass.name} (${pass.trips} trips)",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        },
                        onClick = {
                            selectedPass = pass
                            transferDropdownExpanded = false
                            clearStatus()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = recipient,
            onValueChange = {
                recipient = it
                clearStatus()
            },
            label = { Text("Recipient User ID or Email", fontWeight = FontWeight.Bold, color = Color.Black) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { showConfirmDialog = true },
            enabled = recipient.isNotBlank() && selectedPass != null,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
        ) {
            Text("Transfer Now", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        transferStatus?.let {
            Spacer(Modifier.height(16.dp))
            Text(
                text = it,
                fontWeight = FontWeight.Bold,
                color = if (it.contains("successful", ignoreCase = true)) Color(0xFF2E7D32) else Color.Red
            )
        }
    }

    // Confirm Transfer Dialog
    if (showConfirmDialog && selectedPass != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm Transfer", fontWeight = FontWeight.Bold, color = Color.Black) },
            text = {
                Text(
                    "You are about to transfer the ${selectedPass!!.name} pass to $recipient.",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    coroutineScope.launch {
                        val success = onTransfer(recipient.trim(), selectedPass!!)
                        if (!success) clearStatus()
                    }
                }) {
                    Text("Confirm", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PassManagerScreenPreview() {
    val previewPasses = remember {
        mutableStateListOf(
            TravelPass("Daily Pass", 3),
            TravelPass("Weekly Pass", 15),
            TravelPass("Monthly Pass", 60)
        )
    }
    var previewTransferStatus by remember { mutableStateOf<String?>(null) }

    TripTransferTheme {
        TransferPassScreen(
            availablePasses = previewPasses,
            onTransfer = { _, pass ->
                previewPasses.remove(pass)
                val remainingList = if (previewPasses.isEmpty()) {
                    "No passes left"
                } else {
                    previewPasses.joinToString { "${it.name} (${it.trips} trips)" }
                }
                previewTransferStatus = "Transfer successful! Remaining passes: $remainingList"
                true
            },
            transferStatus = previewTransferStatus,
            clearStatus = { previewTransferStatus = null }
        )
    }
}

@Composable
fun TripTransferTheme(content: @Composable () -> Unit) {
    TODO("Not yet implemented")
}

