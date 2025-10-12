package auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.etrip1.R
// Assuming you have this import correct
// import com.example.etrip1.auth.AuthManager

// --- Placeholder for AuthManager if needed for compilation ---
// You should replace this with your actual implementation.
object AuthManager {
    fun resetPassword(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        // Mock implementation
        if (email.contains("@")) onSuccess() else onError("Invalid email format.")
    }
    // Add other methods like login if necessary for compilation
    // fun login(...) {}
}
// -----------------------------------------------------------

// --- Define Colors for the Transparent Card Look ---
private val TransparentCardBackground = Color.White.copy(alpha = 0.2f)
private val TitleColor = Color.White // Reset Password Title
private val TextFieldLabelColor = Color.White.copy(alpha = 0.8f) // Label text
private val TextFieldContentColor = Color.White // Input text
private val TextFieldBorderColor = Color.White.copy(alpha = 0.7f) // Unfocused border
private val TextFieldFocusedBorderColor = Color(0xFFFFCC80) // Focused border color (Yellowish)
private val SendResetLinkButtonColor = Color(0xFFFF7043) // Orange (Kept the same)
private val BackToLoginLinkColor = Color(0xFFFF7043) // Orange (Kept the same)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit,
    onSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    // Custom text field colors for the transparent card look


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. Full-screen background image
        Image(
            painter = painterResource(id = R.drawable.outsidepage), // The full bus illustration banner
            contentDescription = "Forgot Password Banner",
            contentScale = ContentScale.Crop, // Crop to cover the entire screen
            modifier = Modifier.fillMaxSize()
        )

        // 2. Scrolling content for the form
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Spacer to push the card down (Adjust this height to center the card over the bus area)
            Spacer(modifier = Modifier.height(340.dp))

            // ---------- MAIN TRANSPARENT CARD ----------
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.15f) // transparent card
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ){
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title: "Reset Password" (White Bold)
                    Text(
                        text = "Reset Password",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = TitleColor,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Email Input (White text/label)
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFFFF9800),
                            unfocusedBorderColor = Color(0xFFBBDEFB),
                            cursorColor = Color.White,
                            focusedLabelColor = Color(0xFFFF9800),
                            unfocusedLabelColor = Color.White
                        )
                    )

                    // Send Reset Link Button (Kept Orange)
                    Button(
                        onClick = {
                            if (email.isBlank()) {
                                errorMessage = "Please enter your email"
                                return@Button
                            }

                            isLoading = true
                            errorMessage = null
                            successMessage = null

                            AuthManager.resetPassword(
                                email = email,
                                onSuccess = {
                                    isLoading = false
                                    successMessage = "Password reset email sent. Please check your inbox."
                                    onSuccess()
                                },
                                onError = {
                                    isLoading = false
                                    errorMessage = it
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SendResetLinkButtonColor),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Send Reset Link", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    errorMessage?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    successMessage?.let {
                        Text(
                            text = it,
                            color = Color.Green, // Use green for success messages
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // Back to Login Link (Kept Orange)
                    TextButton(onClick = onBackToLogin) {
                        Text("Back to Login", color = Color.LightGray, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Preview(
    name = "Forgot Password Screen Preview",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ForgotPasswordScreenPreview() {
    MaterialTheme {
        Surface {
            ForgotPasswordScreen(
                onBackToLogin = {},
                onSuccess = {}
            )
        }
    }
}