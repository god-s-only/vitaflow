package com.vitaflow.app.domain.usecase.nutrition

import android.graphics.Bitmap
import com.vitaflow.app.domain.repository.NutritionFoodRepository
import com.vitaflow.app.presentation.ui.features.capture.FoodAnalysisResult
import javax.inject.Inject

class AnalyzeFoodImageUseCase @Inject constructor(private val repository: NutritionFoodRepository) {
    suspend operator fun invoke(bitmap: Bitmap): Result<FoodAnalysisResult>{
        return repository.analyzeFoodImage(bitmap)
    }
}