package com.example.etrip1.triptransfer

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.etrip1.R
import kotlinx.coroutines.launch

private val PrimaryBlue = Color(0xFF1565C0)
private val AccentOrange = Color(0xFFFF9800)
private val BackgroundLight = Color(0xFFF2F2F7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferPassScreen(viewModel: TripTransferViewModel = viewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
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
    var showShareSheet by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(availablePasses, selectedPass) {
        selectedPassObj = availablePasses.find { it.id == selectedPass }
    }

    val integratedGradientBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFFF7043), Color(0xFFFF9043), Color(0xFF1E88E5)),
        endY = 2000f
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = integratedGradientBrush)
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(id = R.drawable.transfer),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .align(Alignment.TopCenter),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(180.dp))

                // 🔹 USER ID CARD — replaced Copy button with Share
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0E5F5)),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    if (currentUserId.isNotBlank()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Your User ID:", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                Text(
                                    text = currentUserId,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = PrimaryBlue
                                )
                            }

                            IconButton(onClick = { showShareSheet = true }) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share UID",
                                    tint = AccentOrange
                                )
                            }
                        }
                    }
                }

                // 🔹 Active Passes section (unchanged)
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Active Passes",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                if (availablePasses.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No passes available for transfer.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 12.dp),
                        modifier = Modifier.fillMaxWidth().heightIn(max = 220.dp)
                    ) {
                        items(availablePasses) { pass ->
                            Card(
                                modifier = Modifier.fillMaxWidth().height(100.dp),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(PrimaryBlue, Color(0xFF1976D2))
                                            ),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(60.dp)
                                            .background(
                                                brush = Brush.verticalGradient(
                                                    colors = listOf(AccentOrange, Color(0xFFFFB74D))
                                                ),
                                                shape = RoundedCornerShape(
                                                    topEnd = 16.dp,
                                                    bottomEnd = 16.dp
                                                )
                                            )
                                            .align(Alignment.CenterEnd)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxSize().padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(pass.type.uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                                            Text("Active Pass", color = Color.White.copy(alpha = 0.9f), fontSize = 10.sp)
                                        }
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .size(55.dp)
                                                .background(color = AccentOrange, shape = RoundedCornerShape(27.5.dp))
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(pass.tripsRemaining.toString(), color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                                                Text("trips", color = Color.White, fontSize = 8.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 🔹 Transfer Form (unchanged)
                Spacer(modifier = Modifier.height(20.dp))
                Text("Transfer Form", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(8.dp))

                val visibleTextFieldColors = outlinedTextFieldColors(
                    focusedTextColor = Color.Black,
                    unfocusedLabelColor = Color.DarkGray,
                    focusedLabelColor = PrimaryBlue,
                    unfocusedBorderColor = Color.Gray,
                    focusedBorderColor = PrimaryBlue,
                    containerColor = Color(0xFFF0E5F5)
                )

                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                ) {
                    TextField(
                        value = selectedPassObj?.type ?: "Select a pass",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        label = { Text("Choose Pass") },
                        colors = visibleTextFieldColors
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
                    singleLine = true,
                    colors = visibleTextFieldColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = tripsToTransfer,
                    onValueChange = { tripsToTransfer = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Number of Trips to Transfer") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = visibleTextFieldColors
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
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentOrange,
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
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // 🔹 Confirm Dialog (same)
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

    // 🔹 Share Sheet Modal (from second code)
    if (showShareSheet) {
        ModalBottomSheet(
            onDismissRequest = { showShareSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Share Your UID", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    fun shareApp(packageName: String, appName: String) {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            setPackage(packageName)
                            putExtra(Intent.EXTRA_TEXT, "My UID: $currentUserId")
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("$appName not installed.")
                            }
                        }
                        showShareSheet = false
                    }

                    IconButton(onClick = { shareApp("com.whatsapp", "WhatsApp") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_whatsapp),
                            contentDescription = "WhatsApp",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    IconButton(onClick = { shareApp("com.facebook.katana", "Facebook") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_facebook),
                            contentDescription = "Facebook",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    IconButton(onClick = { shareApp("com.twitter.android", "X") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_x),
                            contentDescription = "X",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    IconButton(onClick = {
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:")
                            putExtra(Intent.EXTRA_SUBJECT, "My UID")
                            putExtra(Intent.EXTRA_TEXT, "Here’s my UID: $currentUserId")
                        }
                        context.startActivity(emailIntent)
                        showShareSheet = false
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_email),
                            contentDescription = "Email",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                OutlinedButton(onClick = {
                    clipboardManager.setText(AnnotatedString(currentUserId))
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("User ID copied!")
                    }
                    showShareSheet = false
                }) {
                    Text("📋 Copy UID")
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
