package com.example.etrip1.triptransfer



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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import triptransfer.TravelPass


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferPassScreen(passManager: PassManager) {
    val coroutineScope = rememberCoroutineScope()
    var selectedPass by remember { mutableStateOf<TravelPass?>(null) }
    var transferDropdownExpanded by remember { mutableStateOf(false) }
    var recipient by remember { mutableStateOf("") }
    var transferStatus by remember { mutableStateOf<String?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var availablePasses by remember { mutableStateOf(passManager.getAvailablePasses()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "Available Passes",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (availablePasses.isEmpty()) {
            Text("No passes available", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
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
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = pass.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${pass.trips} trips", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Transfer Pass", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = transferDropdownExpanded,
            onExpandedChange = { transferDropdownExpanded = !transferDropdownExpanded }
        ) {
            TextField(
                value = selectedPass?.name ?: "Select a pass to transfer",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = transferDropdownExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                label = { Text("Choose Pass") }
            )
            ExposedDropdownMenu(
                expanded = transferDropdownExpanded,
                onDismissRequest = { transferDropdownExpanded = false }
            ) {
                availablePasses.forEach { pass ->
                    DropdownMenuItem(
                        text = { Text("${pass.name} (${pass.trips} trips)") },
                        onClick = {
                            selectedPass = pass
                            transferDropdownExpanded = false
                            transferStatus = null
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
                transferStatus = null
            },
            label = { Text("Recipient User ID or Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { showConfirmDialog = true },
            enabled = recipient.isNotBlank() && selectedPass != null,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
        ) {
            Text("Transfer Now", fontWeight = FontWeight.Bold)
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

    if (showConfirmDialog && selectedPass != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm Transfer", fontWeight = FontWeight.Bold) },
            text = { Text("You are about to transfer the ${selectedPass!!.name} pass to $recipient.") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    coroutineScope.launch {
                        val success = passManager.transferPass(recipient.trim(), selectedPass!!)
                        if (success) {
                            availablePasses = passManager.getAvailablePasses()
                            transferStatus = "Transfer successful!"
                        } else {
                            transferStatus = "Transfer failed."
                        }
                    }
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
