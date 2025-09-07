package com.example.taxi_app.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.taxi_app.data.AppMode
import com.example.taxi_app.data.User
import com.google.gson.Gson

class AuthManager(private val context: Context) {
    
    companion object {
        private const val PREFS_NAME = "taxi_app_auth"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_DATA = "user_data"
        private const val KEY_APP_MODE = "app_mode"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    
    fun saveAuthData(token: String, user: User, appMode: AppMode) {
        prefs.edit().apply {
            putString(KEY_AUTH_TOKEN, token)
            putString(KEY_USER_DATA, gson.toJson(user))
            putString(KEY_APP_MODE, appMode.name)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
        android.util.Log.d("TaxiApp", "Auth data saved: ${user.name}, mode: $appMode")
    }
    
    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }
    
    fun getSavedUserId(): String? {
        val user = getUserData()
        return user?.id
    }
    
    fun getSavedUserType(): String? {
        val user = getUserData()
        return user?.role
    }
    
    fun getUserData(): User? {
        val userJson = prefs.getString(KEY_USER_DATA, null)
        return if (userJson != null) {
            try {
                gson.fromJson(userJson, User::class.java)
            } catch (e: Exception) {
                android.util.Log.e("TaxiApp", "Error parsing user data: ${e.message}")
                null
            }
        } else null
    }
    
    fun getAppMode(): AppMode? {
        val modeString = prefs.getString(KEY_APP_MODE, null)
        return if (modeString != null) {
            try {
                AppMode.valueOf(modeString)
            } catch (e: Exception) {
                android.util.Log.e("TaxiApp", "Error parsing app mode: ${e.message}")
                null
            }
        } else null
    }
    
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && getAuthToken() != null
    }
    
    fun clearAuthData() {
        prefs.edit().apply {
            remove(KEY_AUTH_TOKEN)
            remove(KEY_USER_DATA)
            remove(KEY_APP_MODE)
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }
        android.util.Log.d("TaxiApp", "Auth data cleared")
    }
    
    fun hasValidSession(): Boolean {
        return isLoggedIn() && getAuthToken() != null && getUserData() != null && getAppMode() != null
    }
}
