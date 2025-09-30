package com.example.etrip1.qrcode

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun QrCodeScreen() {
    val viewModel: QrCodeViewModel = viewModel()
    val userPasses by viewModel.userPasses.collectAsState()
    val selectedPass by viewModel.selectedPass.collectAsState()
    val qrCodeBitmap by viewModel.qrCodeBitmap.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF8F9FA)), // Light blue-gray background
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your Ticket",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = Color(0xFF1565C0), // Blue text
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (userPasses.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Text(
                    text = "No active passes. Purchase a pass to generate a ticket.",
                    modifier = Modifier.padding(24.dp),
                    color = Color(0xFF1565C0), // Blue text
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            // Pass selector
            PassSelector(
                passes = userPasses,
                selectedPass = selectedPass,
                onPassSelected = { viewModel.selectPass(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // QR code display
            if (qrCodeBitmap != null) {
                Card(
                    modifier = Modifier.size(280.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = qrCodeBitmap!!.asImageBitmap(),
                            contentDescription = "QR Code",
                            modifier = Modifier.size(250.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Show this code to the driver when boarding",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF1565C0), // Blue text
                        fontWeight = FontWeight.Medium
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                selectedPass?.let { pass ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1565C0) // Blue background
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${pass.type.replaceFirstChar { it.uppercase() }} Pass",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${pass.tripsRemaining} trips remaining",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color(0xFFFF9800), // Orange text
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFFF9800), // Orange progress indicator
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF1565C0) // Blue text
                ),
                border = null
            ) {
                Text(
                    selectedPass?.type?.replaceFirstChar { it.uppercase() } ?: "Select Pass",
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Select pass",
                    tint = Color(0xFFFF9800) // Orange icon
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
                                color = Color(0xFF1565C0) // Blue text
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