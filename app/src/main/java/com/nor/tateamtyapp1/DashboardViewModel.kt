package com.nor.tateamtyapp1

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

data class DueChild(
    val id: String,
    val name: String,
    val vaccineName: String
)

data class DashboardStats(
    val totalChildren: Int = 0,
    val dueTodayCount: Int = 0,
    val childrenDueToday: List<DueChild> = emptyList()
)

class DashboardViewModel : ViewModel() {

    private val _stats = MutableStateFlow(DashboardStats())
    val stats: StateFlow<DashboardStats> = _stats.asStateFlow()

    init {
        fetchDashboardData()
    }

    private fun fetchDashboardData() {
        val db = Firebase.firestore

        db.collection("children").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("Dashboard", "Error fetching data: ${error.message}")
                return@addSnapshotListener
            }
            
            if (snapshot != null) {
                val total = snapshot.size()
                
                // الحصول على تاريخ اليوم الحالي
                val today = Calendar.getInstance()
                val todayYear = today[Calendar.YEAR]
                val todayDay = today[Calendar.DAY_OF_YEAR]

                val dueList = mutableListOf<DueChild>()

                for (doc in snapshot.documents) {
                    val nextDate = doc.getTimestamp("nextDoseDate")?.toDate()
                    
                    if (nextDate != null) {
                        val doseCal = Calendar.getInstance().apply { time = nextDate }
                        
                        // التحقق مما إذا كان التاريخ يطابق اليوم (أو حتى لو كان قديماً "Overdue" ولم يُطعم بعد)
                        // هذا يضمن ظهور الرقم بشكل صحيح للموظف
                        val isDueToday = (doseCal[Calendar.YEAR] == todayYear) && 
                                       (doseCal[Calendar.DAY_OF_YEAR] == todayDay)
                        
                        val isOverdue = nextDate.before(today.time) && !isDueToday

                        if (isDueToday || isOverdue) {
                            dueList.add(
                                DueChild(
                                    id = doc.id,
                                    name = doc.getString("name") ?: "Unknown",
                                    vaccineName = doc.getString("nextDoseName") ?: "Next Dose"
                                )
                            )
                        }
                    }
                }

                Log.d("Dashboard", "Updating stats: Total=$total, Due=${dueList.size}")

                _stats.value = DashboardStats(
                    totalChildren = total, 
                    dueTodayCount = dueList.size,
                    childrenDueToday = dueList
                )
            }
        }
    }
}
