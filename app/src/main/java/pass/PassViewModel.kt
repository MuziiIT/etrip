package com.example.etrip1.pass

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class PassViewModel : ViewModel() {
    fun startPayment(context: Context, amount: Double, passType: String, onUrlReady: (String) -> Unit, onError: (String) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            onError("User not logged in")
            return
        }

        val repository = PassRepository(context)
        viewModelScope.launch {
            repository.createPayment(amount, passType, userId, onUrlReady, onError)
        }
    }
}
