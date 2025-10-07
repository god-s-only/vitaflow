package com.vitaflow.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "vita_flow_session")

@Singleton
class VitaFlowSession @Inject constructor(private val context: Context) {

    private object PreferencesKeys {
        val TOKEN = stringPreferencesKey("token")
    }

    suspend fun storeToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TOKEN] = token
        }
    }

    suspend fun getToken(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.TOKEN]
        }.first()
    }

    fun getTokenFlow(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.TOKEN]
        }
    }

    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.TOKEN)
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}