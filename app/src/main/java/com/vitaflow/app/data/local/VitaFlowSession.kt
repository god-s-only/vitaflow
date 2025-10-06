package com.vitaflow.app.data.local

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import androidx.core.content.edit

class VitaFlowSession @Inject constructor(context: Context){
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("vita_flow_session", Context.MODE_PRIVATE)

    fun storeToken(token: String) {
        sharedPreferences.edit { putString("token", token) }
    }

    fun getToken(): String? {
        return sharedPreferences.getString("token", null)
    }

    fun clearToken() {
        sharedPreferences.edit { remove("token") }
    }
}