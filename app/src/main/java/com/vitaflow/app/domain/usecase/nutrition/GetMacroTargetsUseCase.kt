package com.vitaflow.app.domain.usecase.nutrition

import com.vitaflow.app.domain.repository.NutritionFoodRepository
import javax.inject.Inject

class GetMacroTargetsUseCase @Inject constructor(
    private val repository: NutritionFoodRepository
) {
    suspend operator fun invoke(): Triple<Int, Int, Int> {
        return repository.getMacroTargets()
    }
}