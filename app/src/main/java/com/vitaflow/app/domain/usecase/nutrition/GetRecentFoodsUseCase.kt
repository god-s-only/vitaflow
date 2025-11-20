package com.vitaflow.app.domain.usecase.nutrition

import com.vitaflow.app.domain.models.Food
import com.vitaflow.app.domain.repository.NutritionFoodRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentFoodsUseCase @Inject constructor(
    private val repository: NutritionFoodRepository
) {
    suspend operator fun invoke(limit: Int = 10): Flow<List<Food>> {
        return repository.getRecentFoods(limit)
    }
}