package com.example.etrip1.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.etrip1.R
import com.example.etrip1.auth.AuthManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
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
        // ---------- TOP BANNER IMAGE WITH FLOATING LOGO ----------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.picture),
                contentDescription = "Register Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Small floating circular logo on top-left
            Box(
                modifier = Modifier
                    .size(60.dp) // smaller like other pages
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
                        text = "Create Account",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color(0xFF0D47A1),
                            fontWeight = FontWeight.Bold
                        )
                    )

                    // OutlinedTextField with focused orange border and blue label
                    val textFieldColors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF7043),  // orange border
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color(0xFF0D47A1),  // blue label
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = Color(0xFF0D47A1)          // blue cursor
                    )

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = textFieldColors
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = textFieldColors
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = textFieldColors
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Register Button
                    Button(
                        onClick = {
                            if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
                                errorMessage = "Please fill all fields"
                                return@Button
                            }
                            if (password != confirmPassword) {
                                errorMessage = "Passwords don't match"
                                return@Button
                            }
                            if (password.length < 6) {
                                errorMessage = "Password must be at least 6 characters"
                                return@Button
                            }

                            isLoading = true
                            errorMessage = null

                            AuthManager.register(
                                email = email,
                                password = password,
                                fullName = fullName,
                                onSuccess = onRegisterSuccess,
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
                            Text("Register", fontWeight = FontWeight.Bold)
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

                    // Navigate to Login
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Already have an account?", color = Color.Gray)
                        TextButton(onClick = onNavigateToLogin) {
                            Text("Login", color = Color(0xFFFF7043))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(onRegisterSuccess = {}, onNavigateToLogin = {})
}