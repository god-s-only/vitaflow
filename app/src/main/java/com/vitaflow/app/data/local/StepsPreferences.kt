package com.vitaflow.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.stepsDataStore: DataStore<Preferences> by preferencesDataStore(name = "steps_preferences")

private object PREFERENCES_KEY {
    val TOKEN = intPreferencesKey(name = "steps")
}

@Singleton
class StepsPreferences @Inject constructor(@ApplicationContext private val context: Context) {
    suspend fun storeStepsTarget(target: Int){
        context.stepsDataStore.edit {
            it[PREFERENCES_KEY.TOKEN] = target
        }
    }
    suspend fun getStepsTarget(): Int?{
        return context.stepsDataStore.data.map {
            it[PREFERENCES_KEY.TOKEN]
        }.first()
    }
}