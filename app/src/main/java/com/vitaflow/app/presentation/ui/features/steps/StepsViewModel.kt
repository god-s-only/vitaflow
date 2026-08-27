package com.vitaflow.app.presentation.ui.features.steps

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaflow.app.common.Resource
import com.vitaflow.app.domain.models.StepsData
import com.vitaflow.app.domain.usecase.steps.CheckHealthConnectAvailabilityUseCase
import com.vitaflow.app.domain.usecase.steps.CheckHealthConnectInstalledUseCase
import com.vitaflow.app.domain.usecase.steps.CheckHealthConnectPermissionsUseCase
import com.vitaflow.app.domain.usecase.steps.GetStepsTarget
import com.vitaflow.app.domain.usecase.steps.GetTodayStepsUseCase
import com.vitaflow.app.domain.usecase.steps.SyncStepsDataUseCase
import com.vitaflow.app.domain.usecase.steps.UpdateTargetStepsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StepsViewModel @Inject constructor(
    private val getTodayStepsUseCase: GetTodayStepsUseCase,
    private val syncStepsDataUseCase: SyncStepsDataUseCase,
    private val updateTargetStepsUseCase: UpdateTargetStepsUseCase,
    private val checkHealthConnectPermissionsUseCase: CheckHealthConnectPermissionsUseCase,
    private val checkHealthConnectAvailabilityUseCase: CheckHealthConnectAvailabilityUseCase,
    private val checkHealthConnectInstalledUseCase: CheckHealthConnectInstalledUseCase,
    private val getStepsTarget: GetStepsTarget
) : ViewModel() {

    private val _uiState = MutableStateFlow<StepsUiState>(StepsUiState.Loading)
    val uiState: StateFlow<StepsUiState> = _uiState.asStateFlow()

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    init {
        checkHealthConnectAvailability()
        getStepsTarget()
    }

    fun getStepsTarget() {
        viewModelScope.launch {
            getStepsTarget.invoke()?.let { target ->
                _uiState.value = when (val currentState = _uiState.value) {
                    is StepsUiState.Success -> {
                        StepsUiState.Success(
                            currentState.data.copy(targetSteps = target)
                        )
                    }
                    else -> currentState
                }
            }
        }
    }

    fun checkHealthConnectAvailability() {
        viewModelScope.launch {
            // Step 1: Check if Health Connect is installed
            val isInstalled = checkHealthConnectInstalledUseCase()
            if (!isInstalled) {
                _uiState.value = StepsUiState.HealthConnectUnavailable
                return@launch
            }

            // Step 2: Check if SDK is available
            val isAvailable = checkHealthConnectAvailabilityUseCase()
            if (!isAvailable) {
                _uiState.value = StepsUiState.HealthConnectUnavailable
                return@launch
            }

            // Step 3: Check permissions
            val hasPermissions = checkHealthConnectPermissionsUseCase().firstOrNull() ?: false
            if (!hasPermissions) {
                _uiState.value = StepsUiState.PermissionRequired
                return@launch
            }

            // Step 4: Everything is OK, load data
            loadStepsData()
        }
    }

    private fun loadStepsData() {
        viewModelScope.launch {
            getTodayStepsUseCase()
                .collect { resource ->
                    _uiState.value = when (resource) {
                        is Resource.Loading -> StepsUiState.Loading
                        is Resource.Success -> {
                            resource.data?.let { StepsUiState.Success(it) }
                                ?: StepsUiState.Error("No data available")
                        }
                        is Resource.Error -> {
                            // If error mentions permissions, show permission screen
                            if (resource.message?.contains("permission", ignoreCase = true) == true) {
                                StepsUiState.PermissionRequired
                            } else {
                                StepsUiState.Error(
                                    resource.message ?: "Unknown error occurred"
                                )
                            }
                        }
                    }
                }
        }
    }

    fun syncData() {
        viewModelScope.launch {
            _syncState.value = SyncState.Syncing

            when (val result = syncStepsDataUseCase()) {
                is Resource.Success -> {
                    _syncState.value = SyncState.Success
                    // Reload data after successful sync
                    loadStepsData()
                }
                is Resource.Error -> {
                    _syncState.value = SyncState.Error(
                        result.message ?: "Sync failed"
                    )
                }
                is Resource.Loading -> {
                    // Already in syncing state
                }
            }

            // Reset sync state after 3 seconds
            kotlinx.coroutines.delay(3000)
            _syncState.value = SyncState.Idle
        }
    }

    fun updateTargetSteps(targetSteps: Int) {
        viewModelScope.launch {
            when (val result = updateTargetStepsUseCase(targetSteps)) {
                is Resource.Success -> {
                    loadStepsData()
                }
                is Resource.Error -> {
                    // Handle error - you might want to show a snackbar
                }
                is Resource.Loading -> {
                    // Handle loading state if needed
                }
            }
        }
    }

    fun onPermissionsGranted() {
        // After permissions granted, try to sync then load data
        viewModelScope.launch {
            _uiState.value = StepsUiState.Loading
            syncData()
        }
    }

    fun retry() {
        checkHealthConnectAvailability()
    }

    fun openHealthConnectInstall(context: Context) {
        try {
            // UI concern: build the Play Store intent in the presentation layer
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            _uiState.value = StepsUiState.Error("Could not open Play Store")
        }
    }

    fun getRequiredPermissions(): Set<String> {
        return setOf(
            "android.permission.health.READ_STEPS",
            "android.permission.health.READ_DISTANCE",
            "android.permission.health.READ_ACTIVE_CALORIES_BURNED",
            "android.permission.health.READ_EXERCISE"
        )
    }
}

sealed class StepsUiState {
    object Loading : StepsUiState()
    data class Success(val data: StepsData) : StepsUiState()
    data class Error(val message: String) : StepsUiState()
    object PermissionRequired : StepsUiState()
    object HealthConnectUnavailable : StepsUiState()
}

sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    object Success : SyncState()
    data class Error(val message: String) : SyncState()
}