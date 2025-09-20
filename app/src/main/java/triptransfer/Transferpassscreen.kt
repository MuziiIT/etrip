package com.example.etrip1.triptransfer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.etrip1.notification.NotificationService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferPassScreen(viewModel: TripTransferViewModel = viewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current   // ✅ store context once

    val availablePasses by viewModel.availablePasses.collectAsState()
    val transferStatus by viewModel.transferStatus.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()

    // keep UI state with rememberSaveable so it's preserved on rotation
    var selectedPass by rememberSaveable { mutableStateOf<String?>(null) } // store passId
    var selectedPassObj by remember { mutableStateOf<TravelPass?>(null) }  // keep object for display
    var dropdownExpanded by rememberSaveable { mutableStateOf(false) }
    var recipient by rememberSaveable { mutableStateOf("") }
    var tripsToTransfer by rememberSaveable { mutableStateOf("1") }
    var showConfirmDialog by rememberSaveable { mutableStateOf(false) }

    // sync selectedPassId -> selectedPassObj when availablePasses update
    LaunchedEffect(availablePasses, selectedPass) {
        selectedPassObj = availablePasses.find { it.id == selectedPass }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "Available Passes",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (availablePasses.isEmpty()) {
            Text(
                "No passes available",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(availablePasses) { pass ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 0.dp),
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
                            Column {
                                Text(pass.type, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                if (pass.validUntil > 0L) {
                                    val daysLeft = ((pass.validUntil - System.currentTimeMillis()) /
                                            (1000 * 60 * 60 * 24)).coerceAtLeast(0)
                                    Text("$daysLeft days left", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            Text("${pass.tripsRemaining} trips", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Transfer Pass", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = { dropdownExpanded = !dropdownExpanded }
        ) {
            TextField(
                value = selectedPassObj?.type ?: "Select a pass",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                label = { Text("Choose Pass") }
            )

            ExposedDropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {
                availablePasses.forEach { pass ->
                    DropdownMenuItem(
                        text = { Text("${pass.type} (${pass.tripsRemaining} trips)") },
                        onClick = {
                            selectedPass = pass.id
                            selectedPassObj = pass
                            dropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = recipient,
            onValueChange = { recipient = it },
            label = { Text("Recipient User ID (UID only)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = tripsToTransfer,
            onValueChange = { tripsToTransfer = it.filter { ch -> ch.isDigit() } },
            label = { Text("Number of Trips to Transfer") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                focusManager.clearFocus(true)
                showConfirmDialog = true

                // 🚀 Show notification immediately when button clicked
               /* NotificationService.showNotification(
                    context = context,
                    title = "Trip Transferred",
                    message = "Your trip has been successfully transferred."
                ) */


            },
            enabled = recipient.isNotBlank() && selectedPassObj != null &&
                    tripsToTransfer.toIntOrNull()?.let { it > 0 && it <= (selectedPassObj?.tripsRemaining ?: 0) } == true &&
                    !isSubmitting,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sending...")
            } else {
                Text("Transfer Now", fontWeight = FontWeight.Bold)
            }
        }

        transferStatus?.let {
            Spacer(Modifier.height(16.dp))
            Text(
                text = it,
                fontWeight = FontWeight.Bold,
                color = if (it.contains("success", ignoreCase = true) || it.contains("created", ignoreCase = true)) Color(0xFF2E7D32) else Color.Red
            )
        }
    }


    // ✅ Confirm Transfer Dialog
    if (showConfirmDialog && selectedPassObj != null) {
        val tripsInt = tripsToTransfer.toIntOrNull() ?: 0
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm Transfer", fontWeight = FontWeight.Bold) },
            text = { Text("Transfer $tripsInt trips from ${selectedPassObj!!.type} to $recipient?") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    coroutineScope.launch {
                        // Perform the transfer
                        viewModel.createTransfer(
                            recipientId = recipient.trim(),
                            passId = selectedPassObj!!.id,
                            trips = tripsInt
                        )

                        // 🚀 Show notification immediately after transfer
                        NotificationService.showNotification(
                            context = context,
                            title = "Trip Transferred",
                            message = "Your trip is successfully transferred."
                        )
                    }
                }) {
                    Text("Confirm")
                }
            },

            dismissButton = {
                TextButton(onClick = {

                    //show notification

                    NotificationService.showNotification(
                        context = context,
                        title = "Trip Transfer Cancelled",
                        message = "Your trip has is not transferred."
                    )
                    showConfirmDialog = false
                }) {
                    Text("Cancel")
                }

            }
        )
    }



}
