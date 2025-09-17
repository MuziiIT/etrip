package com.example.etrip1.pass

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun BuyPassScreen(navController: NavController? = null) {
    val context = LocalContext.current
    val passViewModel: PassViewModel = viewModel()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Buy a Bus Pass", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Daily Pass
        Button(
            onClick = {
                passViewModel.startPayment(
                    context,
                    amount = 20.00,
                    passType = "daily",
                    onUrlReady = { paymentUrl ->
                        val intent = Intent(context, PayFastWebViewActivity::class.java)
                        intent.putExtra("url", paymentUrl)
                        intent.putExtra("passType", "daily")
                        context.startActivity(intent)
                    },
                    onError = { error ->
                        // Show error message
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buy Daily Pass - R20.00 (3 trips)")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Weekly Pass
        Button(
            onClick = {
                passViewModel.startPayment(
                    context,
                    amount = 70.00,
                    passType = "weekly",
                    onUrlReady = { paymentUrl ->
                        val intent = Intent(context, PayFastWebViewActivity::class.java)
                        intent.putExtra("url", paymentUrl)
                        intent.putExtra("passType", "weekly")
                        context.startActivity(intent)
                    },
                    onError = { error ->
                        // Show error message
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buy Weekly Pass - R70.00 (15 trips)")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Monthly Pass
        Button(
            onClick = {
                passViewModel.startPayment(
                    context,
                    amount = 250.00,
                    passType = "monthly",
                    onUrlReady = { paymentUrl ->
                        val intent = Intent(context, PayFastWebViewActivity::class.java)
                        intent.putExtra("url", paymentUrl)
                        intent.putExtra("passType", "monthly")
                        context.startActivity(intent)
                    },
                    onError = { error ->
                        // Show error message
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buy Monthly Pass - R250.00 (60 trips)")
        }
    }
}
