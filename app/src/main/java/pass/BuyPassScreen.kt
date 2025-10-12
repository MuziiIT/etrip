package com.example.etrip1.pass

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.etrip1.R

@Composable
fun BuyPassScreen(navController: NavController? = null) {
    val context = LocalContext.current
    // NOTE: PassViewModel requires proper definition in your project to compile
    val passViewModel: PassViewModel = viewModel()
    val scrollState = rememberScrollState()

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = integratedGradientBrush) // Apply gradient to the entire Box
    ) {
        // --- 1. BANNER ILLUSTRATION (as a background element) ---
        // R.drawable.bus_banner_illustration is the image without the text/logo.
        Image(
            painter = painterResource(id = R.drawable.banner),
            contentDescription = null, // decorative element
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp) // Height for the illustration
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop
        )

        // --- 2. SCROLLABLE CONTENT (Layered over the background/illustration) ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Spacer to push content down past the banner
            Spacer(modifier = Modifier.height(220.dp))

            // Logo (White tint for contrast over the gradient)
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

            }


            Spacer(modifier = Modifier.height(30.dp)) // Spacing before cards start

            // --- PASS CARDS START HERE ---

            // Daily Pass
            PassCard(
                title = "Daily Pass",
                description = "Best for short-term travelers",
                price = "R20.00",
                trips = "3 trips",
                buttonColor = primaryBlue,
                cardColor = cardBackground,
                isPopular = false,
                darkTextColor = accentOrange,
                modifier = Modifier.padding(horizontal = 20.dp)
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

            // Weekly Pass - Highlighted as Popular
            PassCard(
                title = "Weekly Pass",
                description = "Most popular choice for regular riders",
                price = "R70.00",
                trips = "15 trips",
                buttonColor = primaryBlue,
                cardColor = cardBackground,
                isPopular = true,
                darkTextColor = accentOrange,
                modifier = Modifier.padding(horizontal = 20.dp)
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
                description = "Perfect for daily commuters",
                price = "R250.00",
                trips = "60 trips",
                buttonColor = primaryBlue,
                cardColor = cardBackground,
                isPopular = false,
                darkTextColor = accentOrange,
                modifier = Modifier.padding(horizontal = 20.dp)
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

            Spacer(modifier = Modifier.height(20.dp))

            // Note
            Text(
                text = "Note: All passes are valid only for the selected period and they are transferable.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 36.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// --- PassCard Composable (Unchanged from previous version) ---
@Composable
fun PassCard(
    title: String,
    description: String,
    price: String,
    trips: String,
    buttonColor: Color,
    cardColor: Color,
    isPopular: Boolean,
    darkTextColor: Color,
    modifier: Modifier = Modifier,
    onBuyClick: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Title and Popular Tag Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = darkTextColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }

                if (isPopular) {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFC107)), // Yellow color
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.popular),
                                contentDescription = "Popular",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Popular",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Price and Trips Row
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(
                        color = buttonColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    )) {
                        append(price)
                    }
                    append(" • ")
                    withStyle(style = SpanStyle(
                        color = buttonColor,
                        fontWeight = FontWeight.Normal,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    )) {
                        append(trips)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Buy Now Button
            Button(
                onClick = onBuyClick,
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Buy Now", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BuyPassScreenTripleGradientPreview() {
    MaterialTheme {
        BuyPassScreen()
    }
}