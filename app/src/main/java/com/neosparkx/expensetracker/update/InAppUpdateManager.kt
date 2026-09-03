package com.neosparkx.expensetracker.update

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class UpdateState {
    object Idle : UpdateState()
    object Checking : UpdateState()
    object NoUpdateAvailable : UpdateState()
    data class Available(val availableVersionCode: Int) : UpdateState()
    data class Downloading(val bytesDownloaded: Long, val totalBytes: Long) : UpdateState()
    object Downloaded : UpdateState()
    data class Failed(val errorMessage: String) : UpdateState()
}

class InAppUpdateManager(context: Context) {
    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(context.applicationContext)
    
    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    private val installStateUpdatedListener = InstallStateUpdatedListener { state: InstallState ->
        when (state.installStatus()) {
            InstallStatus.DOWNLOADING -> {
                val bytesDownloaded = state.bytesDownloaded()
                val totalBytes = state.totalBytesToDownload()
                _updateState.value = UpdateState.Downloading(bytesDownloaded, totalBytes)
                Log.d(TAG, "In-app update downloading: $bytesDownloaded / $totalBytes")
            }
            InstallStatus.DOWNLOADED -> {
                _updateState.value = UpdateState.Downloaded
                Log.d(TAG, "In-app update downloaded successfully and ready for install.")
            }
            InstallStatus.FAILED -> {
                _updateState.value = UpdateState.Failed("Update download failed with error code: ${state.installErrorCode()}")
                Log.e(TAG, "In-app update failed with error code: ${state.installErrorCode()}")
            }
            InstallStatus.CANCELED -> {
                _updateState.value = UpdateState.Idle
                Log.d(TAG, "In-app update was canceled by user.")
            }
            else -> {
                // Other transient statuses (PENDING, INSTALLING, INSTALLED, UNKNOWN)
            }
        }
    }

    init {
        appUpdateManager.registerListener(installStateUpdatedListener)
    }

    /**
     * Checks for available updates on the Google Play Store.
     * @param activity Host Activity
     * @param launcher ActivityResultLauncher for handling the Google Play confirmation dialog
     * @param isManualCheck If true, triggers UI feedback even if no update is found
     */
    fun checkForUpdate(
        activity: Activity,
        launcher: ActivityResultLauncher<IntentSenderRequest>,
        isManualCheck: Boolean = false,
        onCheckComplete: ((Boolean, String) -> Unit)? = null
    ) {
        _updateState.value = UpdateState.Checking
        
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
                val availability = appUpdateInfo.updateAvailability()
                val isFlexibleAllowed = appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                val isImmediateAllowed = appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)

                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    _updateState.value = UpdateState.Downloaded
                    onCheckComplete?.invoke(true, "An update is already downloaded and ready to install.")
                    return@addOnSuccessListener
                }

                if (availability == UpdateAvailability.UPDATE_AVAILABLE) {
                    val availableVersion = appUpdateInfo.availableVersionCode()
                    _updateState.value = UpdateState.Available(availableVersion)
                    
                    try {
                        val updateType = if (isFlexibleAllowed) AppUpdateType.FLEXIBLE else AppUpdateType.IMMEDIATE
                        val options = AppUpdateOptions.newBuilder(updateType).build()
                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, launcher, options)
                        onCheckComplete?.invoke(true, "New update found! Starting download...")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error starting update flow", e)
                        _updateState.value = UpdateState.Failed(e.localizedMessage ?: "Failed to start update")
                        onCheckComplete?.invoke(false, "Failed to launch update: ${e.localizedMessage}")
                    }
                } else if (availability == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    // Resume active update flow
                    try {
                        val options = AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, launcher, options)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error resuming update flow", e)
                    }
                } else {
                    _updateState.value = UpdateState.NoUpdateAvailable
                    if (isManualCheck) {
                        onCheckComplete?.invoke(false, "Your app is up to date!")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "In-app update check failed: ${exception.localizedMessage}")
                _updateState.value = UpdateState.Idle
                if (isManualCheck) {
                    onCheckComplete?.invoke(false, "Could not check updates: ${exception.localizedMessage ?: "Play Store unavailable"}")
                }
            }
    }

    /**
     * Resumes checking when the activity returns to foreground.
     */
    fun onResume(activity: Activity, launcher: ActivityResultLauncher<IntentSenderRequest>) {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                _updateState.value = UpdateState.Downloaded
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    val options = AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, launcher, options)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to resume immediate update", e)
                }
            }
        }
    }

    /**
     * Triggers Google Play to complete the update, which restarts the app.
     */
    fun completeUpdate() {
        appUpdateManager.completeUpdate()
    }

    /**
     * Clean up listeners.
     */
    fun onDestroy() {
        appUpdateManager.unregisterListener(installStateUpdatedListener)
    }

    companion object {
        private const val TAG = "InAppUpdateManager"
    }
}
