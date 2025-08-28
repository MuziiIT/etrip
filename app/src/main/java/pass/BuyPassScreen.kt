package com.example.etrip1.pass

import androidx.compose.material3.Button
import androidx.compose.material3.Text


import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun BuyPassScreen(navController: NavController? = null, passViewModel: PassViewModel = viewModel()) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Buy a Bus Pass", modifier = Modifier.padding(bottom = 16.dp))

        Button(
            onClick = {
                passViewModel.startPayment(
                    context,
                    amount = 20.00,
                    passType = "daily",
                    onUrlReady = { paymentUrl ->
                        val intent = Intent(context, PayFastWebViewActivity::class.java)
                        intent.putExtra("url", paymentUrl)
                        intent.putExtra("passType", "daily") // <-- Added
                        context.startActivity(intent)
                    },
                    onError = { error ->
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buy Daily Pass")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                passViewModel.startPayment(
                    context,
                    amount = 100.0,
                    passType = "weekly",
                    onUrlReady = { paymentUrl ->
                        val intent = Intent(context, PayFastWebViewActivity::class.java)
                        intent.putExtra("url", paymentUrl)
                        intent.putExtra("passType", "weekly") // <-- Added
                        context.startActivity(intent)
                    },
                    onError = { error ->
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buy Weekly Pass")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                passViewModel.startPayment(
                    context,
                    amount = 100.0,
                    passType = "monthly",
                    onUrlReady = { paymentUrl ->
                        val intent = Intent(context, PayFastWebViewActivity::class.java)
                        intent.putExtra("url", paymentUrl)
                        intent.putExtra("passType", "monthlyy") // <-- Added
                        context.startActivity(intent)
                    },
                    onError = { error ->
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        )  {
            Text("Buy Monthly Pass")
        }
    }
}
