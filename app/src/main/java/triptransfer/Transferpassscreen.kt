package com.example.etrip1.triptransfer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.example.etrip1.R


// Using the same color scheme as the Buy Pass Screen
private val PrimaryBlue = Color(0xFF1565C0)
private val AccentOrange = Color(0xFFFF9800)
private val BackgroundLight = Color(0xFFF2F2F7) // Similar to the light background color

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

    // Define the gradient for the background fade-out effect
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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundLight // Fallback color
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = integratedGradientBrush) // Apply gradient background
                .padding(paddingValues)
        ) {
            // 1. BANNER ILLUSTRATION (Background Layer)
            // Note: R.drawable.banner must contain the clean illustration (no text)
            Image(
                painter = painterResource(id = R.drawable.transfer),
                contentDescription = null, // decorative element
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .align(Alignment.TopCenter),
                contentScale = ContentScale.Crop
            )

            // 2. SCROLLABLE CONTENT (Foreground Layer)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp), // Overall horizontal padding
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // --- TOP BAR (Logo, Title, Slogan) ---
                Spacer(modifier = Modifier.height(20.dp)) // Space from top

                // Logo, Title, and Slogan (Centered over the dark banner area)


                Spacer(modifier = Modifier.height(16.dp))



                // --- USER ID CARD (First Card) ---
                Spacer(modifier = Modifier.height(160.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    // User ID with copy (Modified for card style)
                    if (currentUserId.isNotBlank()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Your User ID:", fontWeight = FontWeight.Bold, color= Color.DarkGray)
                                Text(
                                    text = currentUserId,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = PrimaryBlue
                                )
                            }
                            TextButton(onClick = {
                                clipboardManager.setText(AnnotatedString(currentUserId))
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("User ID copied!")
                                }
                            }) {
                                Text("Copy", color = AccentOrange)
                            }
                        }
                    }
                }

                // --- AVAILABLE PASSES SECTION ---

                Spacer(modifier = Modifier.height(24.dp))

                // Title for passes
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

                // LazyColumn for Passes (limited height for scrolling effect)
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
                        // Fixed height to prevent excessive screen scrolling
                        modifier = Modifier.fillMaxWidth().heightIn(max = 220.dp)
                    ) {
                        items(availablePasses) { pass ->
                            Card(
                                modifier = Modifier.fillMaxWidth().height(100.dp),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                // Pass Card Content (Keeping your existing blue/orange design)
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(PrimaryBlue, Color(0xFF1976D2)),
                                            ),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                ) {
                                    // 🔸 Orange side bar
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(60.dp)
                                            .background(
                                                brush = Brush.verticalGradient(
                                                    colors = listOf(AccentOrange, Color(0xFFFFB74D)),
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

                // Define a set of colors for better visibility against the gradient background
                val visibleTextFieldColors = outlinedTextFieldColors(


                    focusedTextColor = Color.Black, //text color of the words in the text field

                    // The label text color when unfocused
                    unfocusedLabelColor = Color.DarkGray,
                    // The label text color when focused
                    focusedLabelColor = PrimaryBlue,
                    // The color of the border when unfocused
                    unfocusedBorderColor = Color.Gray,
                    // The color of the border when focused
                    focusedBorderColor = PrimaryBlue,
                    // The background color of the TextField (Crucial for visibility)
                    containerColor = cardBackground
                )

                // --- TRANSFER FORM SECTION ---

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    "Transfer Form",
                    fontWeight = FontWeight.Bold,
                    color =  Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth()
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentOrange,
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
                Spacer(modifier = Modifier.height(30.dp)) // Final padding
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