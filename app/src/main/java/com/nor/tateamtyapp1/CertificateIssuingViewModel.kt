package com.nor.tateamtyapp1

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.util.*

sealed class CertificateState {
    object Idle : CertificateState()
    object Loading : CertificateState()
    data class Success(
        val childName: String,
        val dob: Date?,
        val completedVaccines: List<String>,
        val isFullyVaccinated: Boolean
    ) : CertificateState()
    data class Error(val message: String) : CertificateState()
}

class CertificateIssuingViewModel : ViewModel() {

    private val _state = MutableStateFlow<CertificateState>(CertificateState.Idle)
    val state: StateFlow<CertificateState> = _state.asStateFlow()

    // تحديث القائمة المطلوبة لتطابق الجدول الجديد (8 جرعات)
    private val requiredVaccines = listOf(
        "BCG", 
        "Hepatitis B - Birth",
        "Polio (IPV) - Dose 1", 
        "Polio (IPV) - Dose 2",
        "Polio (IPV) - Dose 3", 
        "Measles - Dose 1",
        "MMR - Dose 1",
        "MMR - Dose 2"
    )

    suspend fun fetchChildByNationalId(nationalId: String) {
        _state.value = CertificateState.Loading
        val db = Firebase.firestore

        try {
            val childQuery = db.collection("children")
                .whereEqualTo("nationalId", nationalId)
                .limit(1)
                .get().await()

            if (childQuery.isEmpty) {
                _state.value = CertificateState.Error("Child not found.")
                return
            }

            val childDoc = childQuery.documents[0]
            val childId = childDoc.id
            val childName = childDoc.getString("name") ?: "N/A"
            val dob = childDoc.getTimestamp("dateOfBirth")?.toDate()

            val recordsQuery = db.collection("vaccinationRecords")
                .whereEqualTo("childId", childId)
                .whereEqualTo("status", "GIVEN")
                .get().await()

            val takenVaccines = recordsQuery.documents.mapNotNull { it.getString("vaccineName") }

            // التحقق من اكتمال الـ 8 جرعات بالأسماء الجديدة
            val isFullyVaccinated = requiredVaccines.all { it in takenVaccines }

            _state.value = CertificateState.Success(
                childName = childName,
                dob = dob,
                completedVaccines = takenVaccines,
                isFullyVaccinated = isFullyVaccinated
            )

        } catch (e: Exception) {
            _state.value = CertificateState.Error(e.message ?: "Failed to fetch data")
        }
    }
}
