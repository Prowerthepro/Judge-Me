package com.socialscreencontrol

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.socialscreencontrol.core.util.AppNotifier
import com.socialscreencontrol.presentation.home.HomeViewModel
import com.socialscreencontrol.presentation.navigation.AppNavGraph
import com.socialscreencontrol.service.UsageTrackingService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppNotifier.ensureChannels(this)
        startService(Intent(this, UsageTrackingService::class.java))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                AppNavGraph(homeViewModel = homeViewModel)
            }
        }
    }
}
