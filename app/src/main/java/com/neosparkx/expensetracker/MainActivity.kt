package com.neosparkx.expensetracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neosparkx.expensetracker.ui.theme.MyApplicationTheme
import com.neosparkx.expensetracker.MainViewModel
import com.neosparkx.expensetracker.ui.screens.AppNavigator

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import android.util.Log
import com.neosparkx.expensetracker.update.InAppUpdateManager
import com.neosparkx.expensetracker.update.UpdateState
import kotlinx.coroutines.flow.StateFlow

class MainActivity : FragmentActivity() {
  private lateinit var inAppUpdateManager: InAppUpdateManager
  private lateinit var updateLauncher: ActivityResultLauncher<IntentSenderRequest>

  val updateState: StateFlow<UpdateState>
    get() = inAppUpdateManager.updateState

  fun checkAppUpdate(isManual: Boolean = false, onComplete: ((Boolean, String) -> Unit)? = null) {
    if (::inAppUpdateManager.isInitialized && ::updateLauncher.isInitialized) {
      inAppUpdateManager.checkForUpdate(this, updateLauncher, isManual, onComplete)
    }
  }

  fun completeAppUpdate() {
    if (::inAppUpdateManager.isInitialized) {
      inAppUpdateManager.completeUpdate()
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    inAppUpdateManager = InAppUpdateManager(this)
    updateLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
      if (result.resultCode != RESULT_OK) {
        Log.w("MainActivity", "Update flow dismissed or failed: ${result.resultCode}")
      }
    }

    // Automatic update check on app launch
    inAppUpdateManager.checkForUpdate(this, updateLauncher)
    
    NotificationHelper.scheduleDailyReminders(this)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                101
            )
        }
    }

    WindowCompat.setDecorFitsSystemWindows(window, false)
    enableEdgeToEdge()
    setContent {
      val viewModel: MainViewModel = viewModel()
      val themeSelection by viewModel.themeSelection.collectAsState()
      val darkTheme = when (themeSelection) {
          "Dark" -> true
          "Light" -> false
          else -> androidx.compose.foundation.isSystemInDarkTheme()
      }
      MyApplicationTheme(darkTheme = darkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavigator(viewModel = viewModel, activity = this)
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    if (::inAppUpdateManager.isInitialized && ::updateLauncher.isInitialized) {
      inAppUpdateManager.onResume(this, updateLauncher)
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    if (::inAppUpdateManager.isInitialized) {
      inAppUpdateManager.onDestroy()
    }
  }
}



