package com.example.etrip1.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.etrip1.R
import com.example.etrip1.auth.AuthManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


// Custom colors
private val Blue = Color(0xFF1976D2)    // Blue 700
private val Orange = Color(0xFFFF9800) // Orange 500
private val BlackTransparent = Color.Black.copy(alpha = 0.7f) // Used for unfocused label

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

    Box(modifier = Modifier.fillMaxSize()) {

        // 🔹 Full-screen background image
        Image(
            painter = painterResource(id = R.drawable.outsidepage),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 🔹 Light overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.15f)) // very light overlay
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // eTrip logo and slogan at the top
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 64.dp) // Adjusted padding to match login screen's top spacing
            ) {

            }


            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.15f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )



                    // ⚙️ START MODIFIED TEXT FIELDS FOR WHITE BACKGROUND AND BLACK TEXT ⚙️

                    // Full Name
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        // ➡️ Label is Black
                        label = { Text("Full Name", color = BlackTransparent) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        // ➡️ Input text is Black
                        textStyle = LocalTextStyle.current.copy(color = Color.Black),
                        colors = OutlinedTextFieldDefaults.colors(
                            // ➡️ Set Container/Background Color to White
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            focusedBorderColor = Blue,
                            unfocusedBorderColor = Orange,
                            // ➡️ Cursor is Black for visibility on white background
                            cursorColor = Color.Black,
                            focusedLabelColor = Blue,
                            // ➡️ Unfocused label is Black
                            unfocusedLabelColor = BlackTransparent,
                            // ➡️ Ensure text is Black
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email", color = BlackTransparent) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        textStyle = LocalTextStyle.current.copy(color = Color.Black),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            focusedBorderColor = Blue,
                            unfocusedBorderColor = Orange,
                            cursorColor = Color.Black,
                            focusedLabelColor = Blue,
                            unfocusedLabelColor = BlackTransparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = BlackTransparent) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        textStyle = LocalTextStyle.current.copy(color = Color.Black),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            focusedBorderColor = Blue,
                            unfocusedBorderColor = Orange,
                            cursorColor = Color.Black,
                            focusedLabelColor = Blue,
                            unfocusedLabelColor = BlackTransparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Confirm Password
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password", color = BlackTransparent) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        textStyle = LocalTextStyle.current.copy(color = Color.Black),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            focusedBorderColor = Blue,
                            unfocusedBorderColor = Orange,
                            cursorColor = Color.Black,
                            focusedLabelColor = Blue,
                            unfocusedLabelColor = BlackTransparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                    // ⚙️ END MODIFIED TEXT FIELDS ⚙️

                    Spacer(modifier = Modifier.height(24.dp))

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
                            containerColor = Blue,
                            contentColor = Color.White
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Register")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Error Message
                    errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    // Navigate to Login
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 5.dp)
                    ) {
                        Text(
                            "Already have an account?",
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        TextButton(
                            onClick = onNavigateToLogin,
                            colors = ButtonDefaults.textButtonColors(contentColor = Orange)
                        ) {
                            Text("Login")
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
    RegisterScreen(
        onRegisterSuccess = {},
        onNavigateToLogin = {}
    )
}