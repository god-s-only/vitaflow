package com.vitaflow.app.presentation.ui.features.barcode

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.vitaflow.app.presentation.ui.features.nutrition.mealTypes

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
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Start scanning when screen opens
    LaunchedEffect(Unit) {
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_EAN_13, Barcode.FORMAT_UPC_A)
            .build()

        val scanner = GmsBarcodeScanning.getClient(context, options)

        scanner.startScan()
            .addOnSuccessListener { barcode ->
                barcode.rawValue?.let { code ->
                    viewModel.onBarcodeScanned(code)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    context,
                    "Scan failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                navController.popBackStack()
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
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

                state.error != null -> {
                    ErrorView(
                        error = state.error!!,
                        onRetry = { viewModel.resetScan() },
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

@Composable
fun ScannedProductCard(
    food: com.vitaflow.app.domain.models.NutritionFood,
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
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = food.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Nutrition Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    food.calories?.let {
                        NutrientInfo("Calories", "${it.toInt()}", "kcal")
                    }
                    food.protein?.let {
                        NutrientInfo("Protein", "${it.toInt()}", "g")
                    }
                    food.carbs?.let {
                        NutrientInfo("Carbs", "${it.toInt()}", "g")
                    }
                    food.fat?.let {
                        NutrientInfo("Fat", "${it.toInt()}", "g")
                    }
                }
            }
        }

        Button(
            onClick = onAddToMeal,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            ),
            enabled = !isAddingToMeal
        ) {
            if (isAddingToMeal) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = if (isAddingToMeal) "Adding..." else "Add to Meal",
                fontSize = 16.sp
            )
        }

        OutlinedButton(
            onClick = onScanAgain,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = !isAddingToMeal
        ) {
            Text("Scan Another", fontSize = 16.sp)
        }
    }
}

@Composable
fun NutrientInfo(label: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
        Text(
            text = "per 100$unit",
            fontSize = 10.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun ErrorView(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = error,
            fontSize = 16.sp,
            color = Color.Red
        )
        Button(onClick = onRetry) {
            Text("Try Again")
        }
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
        title = { Text("Select Meal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                mealTypes.forEach { mealType ->
                    OutlinedButton(
                        onClick = { onMealSelected(mealType.type) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = mealType.icon,
                            contentDescription = mealType.name,
                            tint = mealType.color
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(mealType.name)
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
