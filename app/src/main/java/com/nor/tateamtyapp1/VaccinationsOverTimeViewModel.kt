package com.nor.tateamtyapp1

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

data class ChartData(
    val label: String,
    val value: Float
)

sealed class ChartState {
    object Loading : ChartState()
    data class Success(val monthlyData: List<ChartData>, val yearlyData: List<ChartData>) : ChartState()
    data class Error(val message: String) : ChartState()
}

class VaccinationsOverTimeViewModel : ViewModel() {

    private val _chartState = MutableStateFlow<ChartState>(ChartState.Loading)
    val chartState: StateFlow<ChartState> = _chartState.asStateFlow()

    init {
        fetchVaccinationStats()
    }

    private fun fetchVaccinationStats() {
        val db = Firebase.firestore
        
        db.collection("vaccinationRecords")
            .whereEqualTo("status", "GIVEN")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _chartState.value = ChartState.Error(e.message ?: "Error loading data")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                    val monthCounts = mutableMapOf<Int, Int>()
                    val yearCounts = mutableMapOf<Int, Int>()
                    
                    for (doc in snapshot.documents) {
                        val date = doc.getTimestamp("dateAdministered")?.toDate()
                        if (date != null) {
                            val cal = Calendar.getInstance()
                            cal.time = date
                            val year = cal.get(Calendar.YEAR)
                            val month = cal.get(Calendar.MONTH)

                            yearCounts[year] = yearCounts.getOrDefault(year, 0) + 1
                            if (year == currentYear) {
                                monthCounts[month] = monthCounts.getOrDefault(month, 0) + 1
                            }
                        }
                    }

                    val monthly = (0..11).map { monthIndex ->
                        ChartData(getMonthName(monthIndex), monthCounts.getOrDefault(monthIndex, 0).toFloat())
                    }

                    val yearly = (currentYear - 4..currentYear).map { year ->
                        ChartData(year.toString(), yearCounts.getOrDefault(year, 0).toFloat())
                    }

                    _chartState.value = ChartState.Success(monthly, yearly)
                }
            }
    }

    private fun getMonthName(index: Int): String {
        return arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")[index]
    }
}
