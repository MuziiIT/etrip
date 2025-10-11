package com.example.etrip1.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.etrip1.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFA7D7FF), Color(0xFFFFFFFF))
                )
            )
    ) {
        // ---------- TOP IMAGE WITH FLOATING LOGO ----------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.picture),
                contentDescription = "Login Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Floating circular logo (smaller)
            Box(
                modifier = Modifier
                    .size(60.dp)  // Smaller than before
                    .align(Alignment.TopStart)
                    .offset(x = 16.dp, y = 16.dp)
                    .shadow(elevation = 8.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.newlogo),
                    contentDescription = "App Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ---------- MAIN CARD ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color(0xFF0D47A1),
                            fontWeight = FontWeight.Bold
                        )
                    )

                    // Email Input
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF7043),
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color(0xFF0D47A1)
                        )
                    )

                    // Password Input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF7043),
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color(0xFF0D47A1)
                        )
                    )

                    // Forgot password
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onNavigateToForgotPassword) {
                            Text("Forgot Password?", color = Color(0xFFFF7043))
                        }
                    }

                    // Login Button
                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                errorMessage = "Please fill all fields"
                                return@Button
                            }

                            isLoading = true
                            errorMessage = null

                            AuthManager.login(
                                email = email,
                                password = password,
                                onSuccess = onLoginSuccess,
                                onError = {
                                    isLoading = false
                                    errorMessage = it
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF7043),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Login")
                        }
                    }

                    // Error Message
                    errorMessage?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Register section
                    Text("Don't have an account?", color = Color.Gray)
                    TextButton(onClick = onNavigateToRegister) {
                        Text("Register here", color = Color(0xFFFF7043))
                    }
                }
            }
        }
    }
}