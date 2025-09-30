package com.example.etrip1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.etrip1.auth.eTripNavHost
import com.google.firebase.FirebaseApp
import ui.eTripTheme
import viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            eTripTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    // ✅ create the ViewModel here
                    val userViewModel: UserViewModel = viewModel()

                    // pass userViewModel to nav host
                    eTripNavHost(userViewModel)

                }
            }
        }
    }
}


