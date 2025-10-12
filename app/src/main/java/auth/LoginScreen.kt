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
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp

import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

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

        // 🔹 Login form positioned lower on screen
        // 🔹 Login form positioned lower on screen
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 350.dp), // moved further down
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ... rest of the card and form remains the same

            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.15f) // transparent card
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
                        text = "Login",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Sign in to continue",
                        color = Color(0xFFFFE082),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // 🔹 Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFFFF9800),
                            unfocusedBorderColor = Color(0xFFBBDEFB),
                            cursorColor = Color.White,
                            focusedLabelColor = Color(0xFFFF9800),
                            unfocusedLabelColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 🔹 Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFFFF9800),
                            unfocusedBorderColor = Color(0xFFBBDEFB),
                            cursorColor = Color.White,
                            focusedLabelColor = Color(0xFFFF9800),
                            unfocusedLabelColor = Color.White
                        )
                    )

                    Row(

                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement = Arrangement.End

                    ) {

                        TextButton(onClick = onNavigateToForgotPassword) {

                            Text("Forgot Password?", color = Color(0xFFFF9800))

                        }

                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 🔹 Login Button
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFFFF9800),
                                            Color(0xFF2196F3)
                                        )
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "Login",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // 🔹 Error Message
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

                        Text("Register here", color = Color(0xFFFFE082))

                    }

                }
            }
        }
    }
}




@Preview(showBackground = true)

@Composable

fun PreviewLoginScreen() {

// Preview for LoginScreen

    MaterialTheme {

        Surface {

// Pass empty lambda functions for the callback parameters since we're just previewing

            LoginScreen(

                onLoginSuccess = {},

                onNavigateToRegister = {},

                onNavigateToForgotPassword = {}

            )

        }

    }

}