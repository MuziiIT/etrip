package com.example.etrip1.qrcode

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.etrip1.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrCodeScreen() {
    val viewModel: QrCodeViewModel = viewModel()
    val userPasses by viewModel.userPasses.collectAsState()
    val selectedPass by viewModel.selectedPass.collectAsState()
    val qrCodeBitmap by viewModel.qrCodeBitmap.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Full screen background image without excessive zooming
        Image(
            painter = painterResource(id = R.drawable.qrcode), // Use your background image
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit // Changed to Fit to prevent excessive zooming/stretching
        )

        // Semi-transparent overlay for better readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your Ticket",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Scan this when boarding the bus",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (userPasses.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Text(
                        text = "No active passes. Purchase a pass to generate a ticket.",
                        modifier = Modifier.padding(24.dp),
                        color = Color(0xFF1565C0),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Pass Selector Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Select Your Pass",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color(0xFF0D47A1),
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        PassSelector(
                            passes = userPasses,
                            selectedPass = selectedPass,
                            onPassSelected = { viewModel.selectPass(it) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (qrCodeBitmap != null) {
                    // QR Code Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Scan QR Code",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color(0xFF0D47A1),
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Image(
                                bitmap = qrCodeBitmap!!.asImageBitmap(),
                                contentDescription = "QR Code",
                                modifier = Modifier.size(240.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    selectedPass?.let { pass ->
                        // Pass Info Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0).copy(alpha = 0.9f)),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${pass.type.replaceFirstChar { it.uppercase() }} Pass",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${pass.tripsRemaining} trips remaining",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = Color(0xFFFFC107),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Valid until boarding",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                )
                            }
                        }
                    }
                } else {
                    // Loading Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFFFF7043),
                                    strokeWidth = 4.dp,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Generating QR Code...",
                                    color = Color(0xFF0D47A1),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun PassSelector(
    passes: List<Pass>,
    selectedPass: Pass?,
    onPassSelected: (Pass) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF0D47A1)
                ),
                border = null
            ) {
                Text(
                    selectedPass?.let {
                        "${it.type.replaceFirstChar { char -> char.uppercase() }} (${it.tripsRemaining} trips)"
                    } ?: "Select Pass",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Select pass",
                    tint = Color(0xFFFF7043),
                    modifier = Modifier.size(24.dp)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                passes.forEach { pass ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                "${pass.type.replaceFirstChar { it.uppercase() }} (${pass.tripsRemaining} trips)",
                                color = Color(0xFF0D47A1),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        onClick = {
                            onPassSelected(pass)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}