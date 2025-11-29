package com.vitaflow.app.domain.usecase.nutrition

import com.vitaflow.app.domain.models.NutritionFood
import com.vitaflow.app.domain.repository.NutritionFoodRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScanBarcodeUseCase @Inject constructor(private val repository: NutritionFoodRepository) {
    suspend operator fun invoke(upc: String, apiKey: String): Flow<Result<NutritionFood>> {
        if (upc.isBlank()) {
            return kotlinx.coroutines.flow.flow {
                emit(Result.failure(IllegalArgumentException("Barcode cannot be empty")))
            }
        }

        return repository.getFoodProductByUPC(upc, apiKey)
    }
}