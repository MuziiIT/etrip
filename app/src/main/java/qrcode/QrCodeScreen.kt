package com.example.etrip1.qrcode


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

@Composable
fun QrCodeScreen() {
    val viewModel: QrCodeViewModel = viewModel()
    val userPasses by viewModel.userPasses.collectAsState()
    val selectedPass by viewModel.selectedPass.collectAsState()
    val qrCodeBitmap by viewModel.qrCodeBitmap.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your Ticket",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (userPasses.isEmpty()) {
            Text(
                text = "No active passes. Purchase a pass to generate a ticket.",
                modifier = Modifier.padding(16.dp)
            )
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
                Image(
                    bitmap = qrCodeBitmap!!.asImageBitmap(), // ✅ Fix: convert Bitmap → ImageBitmap
                    contentDescription = "QR Code",
                    modifier = Modifier.size(250.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Show this code to the driver when boarding",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                selectedPass?.let { pass ->
                    Text(
                        text = "${pass.type.replaceFirstChar { it.uppercase() }} Pass • ${pass.tripsRemaining} trips remaining",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            } else {
                CircularProgressIndicator()
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

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(selectedPass?.type?.replaceFirstChar { it.uppercase() } ?: "Select Pass")
            Icon(
                imageVector = Icons.Filled.ArrowDropDown, // ✅ Fix: use proper icon reference
                contentDescription = "Select pass"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            passes.forEach { pass ->
                DropdownMenuItem(
                    text = {
                        Text("${pass.type.replaceFirstChar { it.uppercase() }} (${pass.tripsRemaining} trips)")
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
