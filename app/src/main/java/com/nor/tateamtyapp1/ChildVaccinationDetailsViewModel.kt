package com.nor.tateamtyapp1

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

/** ================== Data Models ================== **/
data class ChildVaxInfo(val name: String = "Loading...", val dateOfBirth: Date? = null)

data class VaccinationRecord(
    val vaccineName: String,
    val status: String,
    val ageInMonths: Int, // مضافة للتجميع حسب العمر
    val dueDate: Date? = null,
    val administeredDate: Date? = null
)

sealed class ChildDetailsState {
    object Loading : ChildDetailsState()
    data class Success(
        val childDetails: ChildVaxInfo, 
        val groupedRecords: Map<Int, List<VaccinationRecord>> // تم تغييرها لتكون خارطة مجمعة
    ) : ChildDetailsState()
    data class Error(val message: String) : ChildDetailsState()
}

/** ================== Vaccination Schedule ================== **/
private val vaccineSchedule = listOf(
    "BCG" to 0,
    "Hepatitis B - Birth" to 0,
    "Polio (IPV) - Dose 1" to 2,
    "Polio (IPV) - Dose 2" to 4,
    "Polio (IPV) - Dose 3" to 6,
    "Measles - Dose 1" to 9,
    "MMR - Dose 1" to 12,
    "MMR - Dose 2" to 18
)

/** ================== ViewModel ================== **/
class ChildVaccinationDetailsViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _childDetailsState = MutableStateFlow<ChildDetailsState>(ChildDetailsState.Loading)
    val childDetailsState: StateFlow<ChildDetailsState> = _childDetailsState.asStateFlow()

    private val childId: String = savedStateHandle.get<String>("childId") ?: ""

    init {
        if (childId.isNotBlank())
            fetchChildAndVaccinationData()
        else
            _childDetailsState.value = ChildDetailsState.Error("Child ID is missing.")
    }

    fun confirmVaccination(record: VaccinationRecord, childBirthDate: Date) {
        if (record.status != "OVERDUE" && record.status != "DUE_TODAY") {
            return
        }

        val db = Firebase.firestore
        val newRecord = hashMapOf(
            "childId" to childId,
            "vaccineName" to record.vaccineName,
            "dateAdministered" to FieldValue.serverTimestamp(),
            "status" to "GIVEN"
        )

        db.collection("vaccinationRecords").add(newRecord)
            .addOnSuccessListener {
                updateNextDose(childId, record.vaccineName, childBirthDate)
            }
    }

    private fun updateNextDose(childId: String, currentDoseName: String, birthDate: Date) {
        val currentIndex = vaccineSchedule.indexOfFirst { it.first == currentDoseName }

        if (currentIndex == -1 || currentIndex == vaccineSchedule.lastIndex) {
            Firebase.firestore.collection("children").document(childId)
                .update(mapOf("nextDoseName" to null, "nextDoseDate" to null))
            return
        }

        val nextDose = vaccineSchedule[currentIndex + 1]
        val calendar = Calendar.getInstance().apply {
            time = birthDate
            add(Calendar.MONTH, nextDose.second)
        }

        Firebase.firestore.collection("children").document(childId)
            .update(mapOf(
                "nextDoseName" to nextDose.first,
                "nextDoseDate" to Timestamp(calendar.time),
                "lastNotificationDate" to null
            ))
    }

    private fun fetchChildAndVaccinationData() {
        _childDetailsState.value = ChildDetailsState.Loading
        val db = Firebase.firestore

        db.collection("children").document(childId).get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    val childDetails = ChildVaxInfo(
                        name = doc.getString("name") ?: "N/A",
                        dateOfBirth = (doc["dateOfBirth"] as? Timestamp)?.toDate()
                    )
                    listenToVaccinationRecords(db, childDetails)
                } else {
                    _childDetailsState.value = ChildDetailsState.Error("Child not found.")
                }
            }
    }

    private fun listenToVaccinationRecords(db: com.google.firebase.firestore.FirebaseFirestore, childDetails: ChildVaxInfo) {
        db.collection("vaccinationRecords")
            .whereEqualTo("childId", childId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _childDetailsState.value = ChildDetailsState.Error(e.message ?: "Error")
                    return@addSnapshotListener
                }

                val actualRecords = snapshot?.documents?.associate {
                    it.getString("vaccineName") to (it.get("dateAdministered") as? Timestamp)?.toDate()
                } ?: emptyMap()

                val combinedRecords = vaccineSchedule.map { (vaccineName, ageInMonths) ->
                    val administeredDate = actualRecords[vaccineName]

                    if (administeredDate != null) {
                        VaccinationRecord(vaccineName, "GIVEN", ageInMonths, null, administeredDate)
                    } else {
                        val dueDate = childDetails.dateOfBirth?.let { dob ->
                            Calendar.getInstance().apply {
                                time = dob
                                add(Calendar.MONTH, ageInMonths)
                            }.time
                        }

                        val status = when {
                            dueDate == null -> "PENDING"
                            isSameDay(dueDate, Date()) -> "DUE_TODAY"
                            Date().after(dueDate) -> "OVERDUE"
                            else -> "PENDING"
                        }

                        VaccinationRecord(vaccineName, status, ageInMonths, dueDate, null)
                    }
                }

                // التجميع حسب العمر بالأشهر
                val grouped = combinedRecords.groupBy { it.ageInMonths }

                _childDetailsState.value = ChildDetailsState.Success(childDetails, grouped)
            }
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
