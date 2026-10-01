package com.nor.tateamtyapp1

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// --- تعريف موديل الطفل هنا ليكون متاحاً للجميع ---
data class Child(
    val id: String,
    val name: String,
    val nationalId: String
)

sealed class RecordsState {
    object Loading : RecordsState()
    data class Success(val children: List<Child>) : RecordsState()
    data class Error(val message: String) : RecordsState()
}

class VaccinationRecordsViewModel : ViewModel() {

    private val _recordsState = MutableStateFlow<RecordsState>(RecordsState.Loading)
    val recordsState: StateFlow<RecordsState> = _recordsState.asStateFlow()

    init {
        fetchAllChildren()
    }

    private fun fetchAllChildren() {
        _recordsState.value = RecordsState.Loading
        val db = Firebase.firestore

        db.collection("children")
            .orderBy("name")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _recordsState.value = RecordsState.Error(e.message ?: "Failed to load data")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val childrenList = snapshot.documents.map { doc ->
                        Child(
                            id = doc.id,
                            name = doc.getString("name") ?: "Unknown",
                            nationalId = doc.getString("nationalId") ?: ""
                        )
                    }
                    _recordsState.value = RecordsState.Success(childrenList)
                }
            }
    }
}
