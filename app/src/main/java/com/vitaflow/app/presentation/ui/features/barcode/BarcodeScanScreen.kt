package com.vitaflow.app.presentation.ui.features.barcode

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.vitaflow.app.domain.models.NutritionFood
import com.vitaflow.app.presentation.ui.features.nutrition.mealTypes
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TAG = "BarcodeScanScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeScanScreen(
    navController: NavController,
    mealType: String = "breakfast",
    viewModel: BarcodeScanViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showMealSelector by remember { mutableStateOf(false) }
    var permissionGranted by remember { mutableStateOf(false) }
    var flashEnabled by remember { mutableStateOf(false) }
    var camera by remember { mutableStateOf<Camera?>(null) }

    // Camera permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Camera permission is required", Toast.LENGTH_LONG).show()
            navController.popBackStack()
        }
    }

    // Request permission on launch
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // Handle events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is BarcodeScanEvent.NavigateBack -> {
                    navController.popBackStack()
                }
                is BarcodeScanEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is BarcodeScanEvent.ShowSuccess -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan Barcode") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (permissionGranted && state.scannedProduct == null) {
                        IconButton(onClick = {
                            camera?.cameraControl?.enableTorch(!flashEnabled)
                            flashEnabled = !flashEnabled
                        }) {
                            Icon(
                                imageVector = if (flashEnabled) Icons.Filled.Settings else Icons.Filled.Settings,
                                contentDescription = "Toggle Flash",
                                tint = Color.Black
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                !permissionGranted -> {
                    PermissionRequiredView(
                        onRequestPermission = {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.scannedProduct != null -> {
                    ScannedProductCard(
                        food = state.scannedProduct!!,
                        onAddToMeal = { showMealSelector = true },
                        onScanAgain = { viewModel.resetScan() },
                        isAddingToMeal = state.isAddingToMeal
                    )
                }

                state.isLoading -> {
                    // Show camera preview with loading overlay
                    CameraPreview(
                        onBarcodeScanned = { barcode ->
                            viewModel.onBarcodeScanned(barcode)
                        },
                        onCameraReady = { cam ->
                            camera = cam
                        },
                        isScanning = false // Don't scan while loading
                    )

                    LoadingOverlay(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.error != null -> {
                    // Show camera preview with error overlay
                    CameraPreview(
                        onBarcodeScanned = { barcode ->
                            viewModel.onBarcodeScanned(barcode)
                        },
                        onCameraReady = { cam ->
                            camera = cam
                        },
                        isScanning = true
                    )

                    ErrorOverlay(
                        error = state.error!!,
                        onDismiss = { viewModel.clearError() },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }

                else -> {
                    // Active scanning mode
                    CameraPreview(
                        onBarcodeScanned = { barcode ->
                            viewModel.onBarcodeScanned(barcode)
                        },
                        onCameraReady = { cam ->
                            camera = cam
                        },
                        isScanning = true
                    )

                    // Scanning guide overlay
                    ScanningOverlay(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    // Meal selector dialog
    if (showMealSelector) {
        MealSelectorDialog(
            onDismiss = { showMealSelector = false },
            onMealSelected = { selectedMealType ->
                viewModel.onAddToMeal(selectedMealType, quantity = 100.0)
                showMealSelector = false
            },
            isLoading = state.isAddingToMeal
        )
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreview(
    onBarcodeScanned: (String) -> Unit,
    onCameraReady: (Camera) -> Unit,
    isScanning: Boolean
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var lastScannedBarcode by remember { mutableStateOf<String?>(null) }
    var lastScanTime by remember { mutableStateOf(0L) }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val executor = ContextCompat.getMainExecutor(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                // Preview
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                // Image Analysis for barcode scanning
                val barcodeScanner = BarcodeScanning.getClient()
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                    if (!isScanning) {
                        imageProxy.close()
                        return@setAnalyzer
                    }

                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val image = InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees
                        )

                        barcodeScanner.process(image)
                            .addOnSuccessListener { barcodes ->
                                for (barcode in barcodes) {
                                    barcode.rawValue?.let { barcodeValue ->
                                        val currentTime = System.currentTimeMillis()
                                        // Prevent duplicate scans within 2 seconds
                                        if (barcodeValue != lastScannedBarcode ||
                                            currentTime - lastScanTime > 2000) {
                                            Log.d(TAG, "Barcode detected: $barcodeValue")
                                            lastScannedBarcode = barcodeValue
                                            lastScanTime = currentTime
                                            onBarcodeScanned(barcodeValue)
                                        }
                                    }
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Barcode scanning failed", e)
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                }

                // Camera selector
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    val camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                    onCameraReady(camera)
                } catch (e: Exception) {
                    Log.e(TAG, "Camera binding failed", e)
                }
            }, executor)

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun ScanningOverlay(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Scanning frame
        Box(
            modifier = Modifier
                .size(250.dp)
                .border(3.dp, Color.White, RoundedCornerShape(16.dp))
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.7f)
            )
        ) {
            Text(
                text = "Position barcode within frame",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun LoadingOverlay(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(32.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "Looking up product...",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ErrorOverlay(
    error: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE)
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "⚠️",
                fontSize = 24.sp
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Error",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD32F2F)
                )
                Text(
                    text = error,
                    fontSize = 14.sp,
                    color = Color(0xFFD32F2F)
                )
            }
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    }
}

@Composable
fun PermissionRequiredView(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "📷",
            fontSize = 64.sp
        )
        Text(
            text = "Camera Permission Required",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "We need camera access to scan barcodes",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Grant Permission")
        }
    }
}

@Composable
fun ScannedProductCard(
    food: NutritionFood,
    onAddToMeal: () -> Unit,
    onScanAgain: () -> Unit,
    isAddingToMeal: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "✓",
                    fontSize = 48.sp,
                    color = Color(0xFF4CAF50)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = food.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Nutrition Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    food.calories?.let {
                        NutrientInfo("Calories", "${it.toInt()}", "kcal")
                    } ?: NutrientInfo("Calories", "N/A", "")

                    food.protein?.let {
                        NutrientInfo("Protein", "${it.toInt()}", "g")
                    } ?: NutrientInfo("Protein", "N/A", "")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    food.carbs?.let {
                        NutrientInfo("Carbs", "${it.toInt()}", "g")
                    } ?: NutrientInfo("Carbs", "N/A", "")

                    food.fat?.let {
                        NutrientInfo("Fat", "${it.toInt()}", "g")
                    } ?: NutrientInfo("Fat", "N/A", "")
                }
            }
        }

        Button(
            onClick = onAddToMeal,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            ),
            enabled = !isAddingToMeal
        ) {
            if (isAddingToMeal) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = if (isAddingToMeal) "Adding..." else "Add to Meal",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        OutlinedButton(
            onClick = onScanAgain,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !isAddingToMeal
        ) {
            Text("Scan Another", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun NutrientInfo(label: String, value: String, unit: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4CAF50)
        )
        if (unit.isNotEmpty()) {
            Text(
                text = unit,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun MealSelectorDialog(
    onDismiss: () -> Unit,
    onMealSelected: (String) -> Unit,
    isLoading: Boolean = false
) {
    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = {
            Text(
                "Select Meal",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                mealTypes.forEach { mealType ->
                    Button(
                        onClick = { onMealSelected(mealType.type) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = mealType.color.copy(alpha = 0.1f),
                            contentColor = mealType.color
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = mealType.icon,
                            contentDescription = mealType.name,
                            tint = mealType.color
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            mealType.name,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancel")
            }
        }
    )
}
