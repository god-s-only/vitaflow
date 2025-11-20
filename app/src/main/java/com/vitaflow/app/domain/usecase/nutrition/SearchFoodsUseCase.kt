package com.vitaflow.app.domain.usecase.nutrition

import com.vitaflow.app.domain.models.Food
import com.vitaflow.app.domain.repository.NutritionFoodRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchFoodsUseCase @Inject constructor(
    private val repository: NutritionFoodRepository
) {
    suspend operator fun invoke(query: String): Flow<List<Food>> {
        return repository.searchFoods(query)
    }
}