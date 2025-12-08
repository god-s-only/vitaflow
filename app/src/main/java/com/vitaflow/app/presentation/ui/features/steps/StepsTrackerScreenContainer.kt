package com.vitaflow.app.presentation.ui.features.steps

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.health.connect.client.PermissionController

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepsTrackerScreenContainer(
    viewModel: StepsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val syncState by viewModel.syncState.collectAsState()
    val context = LocalContext.current

    // Health Connect permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        if (granted.containsAll(viewModel.getRequiredPermissions())) {
            viewModel.onPermissionsGranted()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkHealthConnectAvailability()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Steps Tracker",
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.ui.graphics.Color.White
                ),
                actions = {
                    // Only show sync button when data is loaded
                    if (uiState is StepsUiState.Success) {
                        IconButton(
                            onClick = { viewModel.syncData() },
                            enabled = syncState !is SyncState.Syncing
                        ) {
                            when (syncState) {
                                is SyncState.Syncing -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp
                                    )
                                }
                                is SyncState.Success -> {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Sync Success",
                                        tint = StepsColors.Primary
                                    )
                                }
                                else -> {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Sync",
                                        tint = StepsColors.OnSurface
                                    )
                                }
                            }
                        }
                    }

                    IconButton(onClick = { /* Settings */ }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = StepsColors.OnSurface
                        )
                    }
                }
            )
        },
        snackbarHost = {
            if (syncState is SyncState.Error) {
                Snackbar(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text((syncState as SyncState.Error).message)
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is StepsUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is StepsUiState.Success -> {
                    StepsTrackerScreen(stepsData = state.data)
                }

                is StepsUiState.Error -> {
                    ErrorScreen(
                        message = state.message,
                        onRetry = { viewModel.retry() }
                    )
                }

                is StepsUiState.PermissionRequired -> {
                    PermissionScreen(
                        onGrantPermissions = {
                            permissionLauncher.launch(viewModel.getRequiredPermissions())
                        }
                    )
                }

                is StepsUiState.HealthConnectUnavailable -> {
                    HealthConnectUnavailableScreen(
                        onInstallHealthConnect = {
                            viewModel.openHealthConnectInstall(context)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = StepsColors.OnSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = StepsColors.OnSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun PermissionScreen(onGrantPermissions: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = StepsColors.Primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Health Connect Permissions Required",
            style = MaterialTheme.typography.titleLarge,
            color = StepsColors.OnSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "We need permission to access your health data to track your steps",
            style = MaterialTheme.typography.bodyMedium,
            color = StepsColors.OnSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onGrantPermissions) {
            Text("Grant Permissions")
        }
    }
}

@Composable
private fun HealthConnectUnavailableScreen(onInstallHealthConnect: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = StepsColors.AccentOrange
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Health Connect Required",
            style = MaterialTheme.typography.titleLarge,
            color = StepsColors.OnSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Health Connect is not installed on this device. Install it from the Play Store to track your steps.",
            style = MaterialTheme.typography.bodyMedium,
            color = StepsColors.OnSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onInstallHealthConnect) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Install Health Connect")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { /* Skip for now */ }) {
            Text("Skip for now")
        }
    }
}
