package com.vitaflow.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.nutritionDataStore: DataStore<Preferences> by preferencesDataStore(name = "nutrition_preferences")

@Singleton
class NutritionPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val CALORIE_TARGET_KEY = intPreferencesKey("calorie_target")
        private val CARBS_TARGET_KEY = intPreferencesKey("carbs_target")
        private val PROTEIN_TARGET_KEY = intPreferencesKey("protein_target")
        private val FAT_TARGET_KEY = intPreferencesKey("fat_target")
        private val WATER_TARGET_KEY = intPreferencesKey("water_target")

        const val DEFAULT_CALORIE_TARGET = 2000
        const val DEFAULT_CARBS_TARGET = 250
        const val DEFAULT_PROTEIN_TARGET = 150
        const val DEFAULT_FAT_TARGET = 65
        const val DEFAULT_WATER_TARGET = 2000
    }

    suspend fun getCalorieTarget(): Int {
        return context.nutritionDataStore.data.map { preferences ->
            preferences[CALORIE_TARGET_KEY] ?: DEFAULT_CALORIE_TARGET
        }.first()
    }

    suspend fun setCalorieTarget(target: Int) {
        context.nutritionDataStore.edit { preferences ->
            preferences[CALORIE_TARGET_KEY] = target
        }
    }

    suspend fun getMacroTargets(): Triple<Int, Int, Int> {
        return context.nutritionDataStore.data.map { preferences ->
            Triple(
                preferences[CARBS_TARGET_KEY] ?: DEFAULT_CARBS_TARGET,
                preferences[PROTEIN_TARGET_KEY] ?: DEFAULT_PROTEIN_TARGET,
                preferences[FAT_TARGET_KEY] ?: DEFAULT_FAT_TARGET
            )
        }.first()
    }

    suspend fun setMacroTargets(carbs: Int, protein: Int, fat: Int) {
        context.nutritionDataStore.edit { preferences ->
            preferences[CARBS_TARGET_KEY] = carbs
            preferences[PROTEIN_TARGET_KEY] = protein
            preferences[FAT_TARGET_KEY] = fat
        }
    }

    suspend fun getWaterTarget(): Int {
        return context.nutritionDataStore.data.map { preferences ->
            preferences[WATER_TARGET_KEY] ?: DEFAULT_WATER_TARGET
        }.first()
    }

    suspend fun setWaterTarget(target: Int) {
        context.nutritionDataStore.edit { preferences ->
            preferences[WATER_TARGET_KEY] = target
        }
    }
}