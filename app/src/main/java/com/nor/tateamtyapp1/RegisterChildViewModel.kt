package com.nor.tateamtyapp1

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    object Success : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}

class RegisterChildViewModel : ViewModel() {

    var registrationState: RegistrationState by mutableStateOf(RegistrationState.Idle)
        private set

    fun registerChild(childName: String, nationalId: String, dateOfBirthStr: String, guardianPhone: String) {
        if (childName.isBlank() || nationalId.isBlank() || dateOfBirthStr.isBlank()) {
            registrationState = RegistrationState.Error("Please fill all required fields.")
            return
        }

        registrationState = RegistrationState.Loading

        viewModelScope.launch {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dobDate = sdf.parse(dateOfBirthStr) ?: throw Exception("Invalid date format")


                val childData = hashMapOf(
                    "name" to childName,
                    "nationalId" to nationalId,
                    "dateOfBirth" to Timestamp(dobDate),
                    "guardianPhone" to guardianPhone,
                    "nextDoseName" to "BCG", // الجرعة الأولى عند الولادة مباشرة
                    "nextDoseDate" to Timestamp(dobDate), // موعدها هو نفس تاريخ الميلاد
                    "createdAt" to FieldValue.serverTimestamp(),
                    "lastNotificationDate" to null
                )

                Firebase.firestore.collection("children").add(childData)
                registrationState = RegistrationState.Success
            } catch (e: Exception) {
                registrationState = RegistrationState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun resetState() {
        registrationState = RegistrationState.Idle
    }
}