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

// بيانات الطفل التي تظهر في نتيجة البحث
data class SearchedChildInfo(
    val id: String,
    val name: String,
    val nationalId: String,
    val phone: String
)

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Success(val results: List<SearchedChildInfo>) : SearchState()
    object NotFound : SearchState()
    data class Error(val message: String) : SearchState()
}

class SearchChildViewModel : ViewModel() {

    var searchState: SearchState by mutableStateOf(SearchState.Idle)
        private set

    fun searchChild(query: String) {
        if (query.isBlank()) return

        searchState = SearchState.Loading

        viewModelScope.launch {
            try {
                val db = Firebase.firestore

                // 1. محاولة البحث بالرقم القومي (دقيق)
                val idQuery = db.collection("children")
                    .whereEqualTo("nationalId", query)
                    .get().await()
                
                // 2. محاولة البحث برقم الهاتف (دقيق)
                val phoneQuery = db.collection("children")
                    .whereEqualTo("guardianPhone", query)
                    .get().await()

                // 3. محاولة البحث بالاسم (بحث جزئي - يبدأ بـ)
                val nameQuery = db.collection("children")
                    .whereGreaterThanOrEqualTo("name", query)
                    .whereLessThanOrEqualTo("name", query + "\uf8ff")
                    .get().await()

                // دمج النتائج مع منع التكرار
                val allDocs = idQuery.documents + phoneQuery.documents + nameQuery.documents
                val uniqueDocs = allDocs.distinctBy { it.id }

                searchState = if (uniqueDocs.isEmpty()) {
                    SearchState.NotFound
                } else {
                    val children = uniqueDocs.map { doc ->
                        SearchedChildInfo(
                            id = doc.id,
                            name = doc.getString("name") ?: "N/A",
                            nationalId = doc.getString("nationalId") ?: "N/A",
                            phone = doc.getString("guardianPhone") ?: "N/A"
                        )
                    }
                    SearchState.Success(children)
                }

            } catch (e: Exception) {
                searchState = SearchState.Error(e.message ?: "Search failed")
            }
        }
    }

    fun resetState() {
        searchState = SearchState.Idle
    }
}
