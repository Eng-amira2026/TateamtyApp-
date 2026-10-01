package com.nor.tateamtyapp1

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.nor.tateamtyapp1.ui.theme.Tateamtyapp1Theme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            setupVaccineWorker()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkNotificationPermission()

        enableEdgeToEdge()
        setContent {
            Tateamtyapp1Theme {
                AppNavigation()
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                setupVaccineWorker()
            }
        } else {
            setupVaccineWorker()
        }
    }

    private fun setupVaccineWorker() {
        val workManager = WorkManager.getInstance(this)

        // 1. الجدولة الدورية (كل 24 ساعة)
        val periodicRequest = PeriodicWorkRequestBuilder<VaccineNotificationWorker>(
            24, TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            "VaccineCheckWork",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )



        val oneTimeRequest = OneTimeWorkRequestBuilder<VaccineNotificationWorker>().build()
        workManager.enqueue(oneTimeRequest)
    }
}
