package com.example.etrip1.pass

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.etrip1.R

@Composable
fun BuyPassScreen(navController: NavController? = null) {
    val context = LocalContext.current
    val passViewModel: PassViewModel = viewModel()
    val scrollState = rememberScrollState()
    val blueColor = Color(0xFF1E88E5)
    val orangeColor = Color(0xFFFFA500)
    val backgroundColor = Color(0xFFFFFDF7)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo Row
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painter = painterResource(id = R.drawable.logoo),
                contentDescription = "Bus Pass",
                tint = Color.Unspecified,
                modifier = Modifier.size(60.dp).clip(CircleShape)
            )
        }

        // Title
        Text(
            text = "Buy a Bus Pass",
            style = MaterialTheme.typography.headlineSmall,
            color = orangeColor,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Choose the right pass for your travel needs and save on trips!",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Daily Pass
        PassCard(
            title = "Daily Pass",
            price = "R20.00",
            trips = "3 trips",
            description = "Best for short-term travelers",
            buttonColor = blueColor
        ) {
            passViewModel.startPayment(
                context,
                amount = 20.0,
                passType = "daily",
                onUrlReady = { paymentUrl ->
                    val intent = Intent(context, PayFastWebViewActivity::class.java)
                    intent.putExtra("url", paymentUrl)
                    intent.putExtra("passType", "daily")
                    context.startActivity(intent)
                },
                onError = {}
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Weekly Pass
        PassCard(
            title = "Weekly Pass",
            price = "R70.00",
            trips = "15 trips",
            description = "Most popular choice for regular riders",
            buttonColor = blueColor
        ) {
            passViewModel.startPayment(
                context,
                amount = 70.0,
                passType = "weekly",
                onUrlReady = { paymentUrl ->
                    val intent = Intent(context, PayFastWebViewActivity::class.java)
                    intent.putExtra("url", paymentUrl)
                    intent.putExtra("passType", "weekly")
                    context.startActivity(intent)
                },
                onError = {}
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Monthly Pass
        PassCard(
            title = "Monthly Pass",
            price = "R250.00",
            trips = "60 trips",
            description = "Perfect for daily commuters",
            buttonColor = blueColor
        ) {
            passViewModel.startPayment(
                context,
                amount = 250.0,
                passType = "monthly",
                onUrlReady = { paymentUrl ->
                    val intent = Intent(context, PayFastWebViewActivity::class.java)
                    intent.putExtra("url", paymentUrl)
                    intent.putExtra("passType", "monthly")
                    context.startActivity(intent)
                },
                onError = {}
            )
        }
    }
}

@Composable
fun PassCard(
    title: String,
    price: String,
    trips: String,
    description: String,
    buttonColor: Color,
    onBuyClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(price, style = MaterialTheme.typography.bodyLarge, color = Color.Black)
            Spacer(modifier = Modifier.height(2.dp))
            Text(trips, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(description, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onBuyClick,
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buy Now", color = Color.White)
            }
        }
    }
}