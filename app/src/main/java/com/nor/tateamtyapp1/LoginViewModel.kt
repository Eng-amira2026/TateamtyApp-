package com.nor.tateamtyapp1

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel : ViewModel() {

    var loginUiState: LoginUiState by mutableStateOf(LoginUiState.Idle)
        private set

    fun loginUser(username: String, password: String) {
        // تنظيف المدخلات من المسافات الزائدة
        val cleanUsername = username.trim()
        val cleanPassword = password.trim()

        if (cleanUsername.isBlank() || cleanPassword.isBlank()) {
            loginUiState = LoginUiState.Error("Username and password cannot be empty.")
            return
        }

        loginUiState = LoginUiState.Loading

        viewModelScope.launch {
            try {
                val db = Firebase.firestore
                
                // البحث عن الموظف في مجموعة "users"
                val querySnapshot = db.collection("users")
                    .whereEqualTo("username", cleanUsername)
                    .whereEqualTo("password", cleanPassword)
                    .limit(1)
                    .get()
                    .await()

                loginUiState = if (!querySnapshot.isEmpty) {
                    LoginUiState.Success
                } else {
                    LoginUiState.Error("Invalid credentials. Please check your data in Firebase.")
                }
            } catch (e: Exception) {
                loginUiState = LoginUiState.Error(e.message ?: "Network error occurred.")
            }
        }
    }

    fun resetState() {
        loginUiState = LoginUiState.Idle
    }
}
