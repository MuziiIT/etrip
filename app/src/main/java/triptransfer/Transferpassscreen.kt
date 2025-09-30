package com.example.etrip1.triptransfer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.example.etrip1.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferPassScreen(viewModel: TripTransferViewModel = viewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val clipboardManager = LocalClipboardManager.current

    val availablePasses by viewModel.availablePasses.collectAsState()
    val transferStatus by viewModel.transferStatus.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()

    var selectedPass by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedPassObj by remember { mutableStateOf<TravelPass?>(null) }
    var dropdownExpanded by rememberSaveable { mutableStateOf(false) }
    var recipient by rememberSaveable { mutableStateOf("") }
    var tripsToTransfer by rememberSaveable { mutableStateOf("1") }
    var showConfirmDialog by rememberSaveable { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(availablePasses, selectedPass) {
        selectedPassObj = availablePasses.find { it.id == selectedPass }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 🔹 Logo
            Image(
                painter = painterResource(id = R.drawable.logoo),
                contentDescription = "App Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .shadow(4.dp, CircleShape)
                    .align(Alignment.Start)
            )

            // 🔹 User ID with copy
            if (currentUserId.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Your User ID:", fontWeight = FontWeight.Bold)
                        Text(
                            text = currentUserId,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                    TextButton(onClick = {
                        clipboardManager.setText(AnnotatedString(currentUserId))
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("User ID copied!")
                        }
                    }) {
                        Text("Copy")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 🔹 Available Passes (modern cards kept!)
            Text(
                text = "Available Passes",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0),
                    fontSize = 20.sp
                ),
                modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
            )

            if (availablePasses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No passes available", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 12.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    items(availablePasses) { pass ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFF1565C0),
                                                Color(0xFF1976D2),
                                            )
                                        )
                                    )
                            ) {
                                // 🔸 Orange side bar
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(60.dp)
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(
                                                    Color(0xFFFF9800),
                                                    Color(0xFFFFB74D),
                                                )
                                            ),
                                            shape = RoundedCornerShape(
                                                topStart = 0.dp,
                                                bottomStart = 0.dp,
                                                topEnd = 16.dp,
                                                bottomEnd = 16.dp
                                            )
                                        )
                                        .align(Alignment.CenterEnd)
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = pass.type.uppercase(),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Active Pass",
                                            color = Color.White.copy(alpha = 0.9f),
                                            fontSize = 10.sp
                                        )
                                    }

                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(55.dp)
                                            .background(
                                                color = Color(0xFFFF9800),
                                                shape = RoundedCornerShape(27.5.dp)
                                            )
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = pass.tripsRemaining.toString(),
                                                color = Color.White,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 18.sp
                                            )
                                            Text(
                                                text = "trips",
                                                color = Color.White,
                                                fontSize = 8.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 🔹 Transfer Form
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Transfer Pass",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0),
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

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
                    label = { Text("Choose Pass") },
                    colors = outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF1565C0),
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                    )
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

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = recipient,
                onValueChange = { recipient = it },
                label = { Text("Recipient User ID (UID only)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = tripsToTransfer,
                onValueChange = { tripsToTransfer = it.filter { ch -> ch.isDigit() } },
                label = { Text("Number of Trips to Transfer") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    focusManager.clearFocus(true)
                    showConfirmDialog = true
                },
                enabled = recipient.isNotBlank() && selectedPassObj != null &&
                        tripsToTransfer.toIntOrNull()?.let {
                            it > 0 && it <= (selectedPassObj?.tripsRemaining ?: 0)
                        } == true && !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1565C0),
                    contentColor = Color.White
                )
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sending...")
                } else {
                    Text("Transfer Now", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            transferStatus?.let {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = it,
                    fontWeight = FontWeight.Bold,
                    color = if (it.contains("success", true) || it.contains("created", true))
                        Color(0xFF2E7D32) else Color.Red
                )
            }
        }
    }

    // 🔹 Confirm Transfer Dialog
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
                        viewModel.createTransfer(
                            recipientId = recipient.trim(),
                            passId = selectedPassObj!!.id,
                            trips = tripsInt
                        )
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
