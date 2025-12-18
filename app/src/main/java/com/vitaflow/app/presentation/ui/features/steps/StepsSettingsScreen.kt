package com.vitaflow.app.presentation.ui.features.steps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

private val PrimaryGreen = Color(0xFF4CAF50)
private val SecondaryBlue = Color(0xFF2196F3)
private val OrangeAccent = Color(0xFFFF9800)
private val PurpleAccent = Color(0xFF9C27B0)
private val BackgroundWhite = Color(0xFFF8F9FA)
private val CardWhite = Color.White
private val TextPrimary = Color(0xFF1A1A1A)
private val TextSecondary = Color(0xFF666666)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepsSettingsScreen(
    navController: NavController,
    viewModel: StepsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var stepsTarget by remember { mutableStateOf("10000") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(true) }

    // Update local state when uiState changes
    LaunchedEffect(uiState) {
        if (uiState is StepsUiState.Success) {
            val data = (uiState as StepsUiState.Success).data
            stepsTarget = data.targetSteps.toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardWhite,
                    titleContentColor = TextPrimary
                ),
                title = {
                    Text(
                        text = "Steps Goal",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            stepsTarget.toIntOrNull()?.let {
                                viewModel.updateTargetSteps(it)
                            }
                            showSuccessDialog = true
                        }
                    ) {
                        Text(
                            text = "Save",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PrimaryGreen
                        )
                    }
                }
            )
        },
        containerColor = BackgroundWhite
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is StepsUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PrimaryGreen
                    )
                }
                is StepsUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header Card
                        item {
                            HeaderCard()
                        }

                        // Daily Steps Goal
                        item {
                            StepsSettingSection(
                                title = "Daily Steps Goal",
                                subtitle = "Your target daily steps",
                                icon = Icons.Default.Settings,
                                iconColor = PrimaryGreen,
                                value = stepsTarget,
                                onValueChange = { stepsTarget = it },
                                isExpanded = isExpanded,
                                onExpandClick = { isExpanded = !isExpanded },
                                quickValues = listOf(5000, 8000, 10000, 12000, 15000),
                                onQuickValueClick = { stepsTarget = it.toString() }
                            )
                        }

                        // Benefits Card
                        item {
                            BenefitsCard()
                        }

                        // Info Card
                        item {
                            InfoCard()
                        }

                        // Bottom spacing
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
                is StepsUiState.Error -> {
                    ErrorContent(
                        message = (uiState as StepsUiState.Error).message,
                        onRetry = { viewModel.retry() }
                    )
                }
                is StepsUiState.PermissionRequired,
                is StepsUiState.HealthConnectUnavailable -> {
                    // Show a message that settings require data access
                    ErrorContent(
                        message = "Please set up Health Connect first to configure your steps goal",
                        onRetry = { navController.popBackStack() }
                    )
                }
            }
        }

        // Success Dialog
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = {
                    showSuccessDialog = false
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = {
                    Text(
                        text = "Goal Updated!",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Text(
                        text = "Your steps goal has been saved successfully. Keep moving to reach your target!",
                        textAlign = TextAlign.Center,
                        color = TextSecondary
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSuccessDialog = false
                            viewModel.retry()
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Continue")
                    }
                },
                containerColor = CardWhite,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
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
            tint = OrangeAccent,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            fontSize = 16.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreen
            )
        ) {
            Text("Go Back")
        }
    }
}

@Composable
private fun HeaderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(PrimaryGreen, SecondaryBlue)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Set Your Daily Target",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Choose a steps goal that motivates you to stay active",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun StepsSettingSection(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    value: String,
    onValueChange: (String) -> Unit,
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    quickValues: List<Int>,
    onQuickValueClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandClick() }
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                iconColor.copy(alpha = 0.1f),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = subtitle,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "$value steps",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = iconColor
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                }
            }

            // Expandable Content
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))

                    // Input Field
                    OutlinedTextField(
                        value = value,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                onValueChange(newValue)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Enter Steps Goal") },
                        suffix = { Text("steps", color = TextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = iconColor,
                            focusedLabelColor = iconColor,
                            cursorColor = iconColor
                        )
                    )

                    // Quick Select Buttons
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Quick Select",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // First row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                quickValues.take(3).forEach { quickValue ->
                                    OutlinedButton(
                                        onClick = { onQuickValueClick(quickValue) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (value == quickValue.toString())
                                                iconColor.copy(alpha = 0.1f) else Color.Transparent,
                                            contentColor = if (value == quickValue.toString())
                                                iconColor else TextSecondary
                                        ),
                                        border = ButtonDefaults.outlinedButtonBorder.copy(
                                            brush = if (value == quickValue.toString())
                                                Brush.linearGradient(colors = listOf(iconColor, iconColor))
                                            else
                                                Brush.linearGradient(colors = listOf(Color.Gray.copy(0.3f), Color.Gray.copy(0.3f)))
                                        )
                                    ) {
                                        Text(
                                            text = "${quickValue / 1000}K",
                                            fontSize = 13.sp,
                                            fontWeight = if (value == quickValue.toString())
                                                FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }

                            // Second row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                quickValues.drop(3).forEach { quickValue ->
                                    OutlinedButton(
                                        onClick = { onQuickValueClick(quickValue) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (value == quickValue.toString())
                                                iconColor.copy(alpha = 0.1f) else Color.Transparent,
                                            contentColor = if (value == quickValue.toString())
                                                iconColor else TextSecondary
                                        ),
                                        border = ButtonDefaults.outlinedButtonBorder.copy(
                                            brush = if (value == quickValue.toString())
                                                Brush.linearGradient(colors = listOf(iconColor, iconColor))
                                            else
                                                Brush.linearGradient(colors = listOf(Color.Gray.copy(0.3f), Color.Gray.copy(0.3f)))
                                        )
                                    ) {
                                        Text(
                                            text = "${quickValue / 1000}K",
                                            fontSize = 13.sp,
                                            fontWeight = if (value == quickValue.toString())
                                                FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                                // Empty spacers for alignment
                                repeat(3 - quickValues.drop(3).size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BenefitsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryGreen.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Benefits of Walking",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            BenefitItem("5,000 steps", "Basic health maintenance")
            BenefitItem("8,000 steps", "Improved cardiovascular health")
            BenefitItem("10,000 steps", "Weight management & fitness")
            BenefitItem("12,000+ steps", "Enhanced athletic performance")
        }
    }
}

@Composable
private fun BenefitItem(steps: String, benefit: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(PrimaryGreen, shape = RoundedCornerShape(3.dp))
                .offset(y = 6.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = steps,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryGreen
            )
            Text(
                text = benefit,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun InfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SecondaryBlue.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = SecondaryBlue,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = "Your goal can be adjusted anytime. Start with a comfortable target and increase gradually.",
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 18.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}