/*package com.example.etrip1.triptransfer

import android.content.Intent
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.etrip1.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferPassScreen(viewModel: TripTransferViewModel = viewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

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

    val textFieldColors = outlinedTextFieldColors(
        focusedBorderColor = Color(0xFFFF7043),
        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
        focusedLabelColor = Color(0xFF0D47A1),
        unfocusedLabelColor = Color.Gray,
        cursorColor = Color(0xFF0D47A1)
    )

    LaunchedEffect(availablePasses, selectedPass) {
        selectedPassObj = availablePasses.find { it.id == selectedPass }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE3F2FD),
                            Color.White
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ---------- BANNER ----------
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.tranferr),
                        contentDescription = "Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f))
                    )

                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "eTrip Transfer",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Transfer your passes quickly and safely",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ---------- SCROLLABLE CONTENT ----------
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        // ---------- USER ID ----------
                        if (currentUserId.isNotBlank()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Your User ID:", fontWeight = FontWeight.Bold)
                                    Text(currentUserId, color = Color.Gray)
                                }

                                Row {
                                    IconButton(onClick = {
                                        clipboardManager.setText(AnnotatedString(currentUserId))
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("User ID copied!")
                                        }
                                    }) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                                    }
                                    IconButton(onClick = {
                                        val shareIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, "My eTrip User ID: $currentUserId")
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                                    }) {
                                        Icon(Icons.Default.Share, contentDescription = "Share")
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // ---------- AVAILABLE PASSES HEADER ----------
                        Text(
                            text = "Available Passes",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0),
                            fontSize = 20.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // ---------- AVAILABLE PASS CARDS ----------
                    items(availablePasses) { pass ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .height(100.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1976D2))
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {

                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(start = 16.dp, end = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {

                                    // ---------- Text Info ----------
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

                                    // ---------- Right Side: Orange Circle + Orange Strip ----------
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Orange Circle
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .size(55.dp)
                                                .background(
                                                    color = Color(0xFFFF7043),
                                                    shape = CircleShape
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

                                        Spacer(modifier = Modifier.width(8.dp))

                                        // Orange Strip
                                        Box(
                                            modifier = Modifier
                                                .width(10.dp)
                                                .fillMaxHeight()
                                                .background(Color(0xFFFF7043))
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        // ---------- TRANSFER FORM ----------
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            "Transfer Pass",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0),
                            fontSize = 20.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        ExposedDropdownMenuBox(
                            expanded = dropdownExpanded,
                            onExpandedChange = { dropdownExpanded = !dropdownExpanded },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            TextField(
                                value = selectedPassObj?.type ?: "Select a pass",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Choose Pass", color = Color(0xFF0D47A1)) },
                                colors = textFieldColors
                            )

                            ExposedDropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false }
                            ) {
                                availablePasses.forEach { pass ->
                                    DropdownMenuItem(
                                        text = { Text("${pass.type} (${pass.tripsRemaining} trips)", color = Color(0xFF0D47A1)) },
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            singleLine = true,
                            colors = textFieldColors
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = tripsToTransfer,
                            onValueChange = { tripsToTransfer = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Number of Trips to Transfer") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            singleLine = true,
                            colors = textFieldColors
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showConfirmDialog = true },
                            enabled = recipient.isNotBlank() && selectedPassObj != null &&
                                    tripsToTransfer.toIntOrNull()?.let {
                                        it > 0 && it <= (selectedPassObj?.tripsRemaining ?: 0)
                                    } == true && !isSubmitting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
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
                                    Color(0xFF2E7D32) else Color.Red,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    // ---------- Confirm Transfer Dialog ----------
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
*\