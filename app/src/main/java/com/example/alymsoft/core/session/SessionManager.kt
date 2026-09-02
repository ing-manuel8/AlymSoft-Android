package com.example.alymsoft.core.session

import android.content.Context
import android.content.SharedPreferences
import com.example.alymsoft.domain.model.User
import com.google.gson.Gson

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREF_NAME = "alymsoft_session_prefs"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_DATA = "user_data"

        @Volatile
        private var INSTANCE: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SessionManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    fun saveUser(user: User) {
        val json = gson.toJson(user)
        prefs.edit().putString(KEY_USER_DATA, json).apply()
    }

    fun getUser(): User? {
        val json = prefs.getString(KEY_USER_DATA, null) ?: return null
        return try {
            gson.fromJson(json, User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun isAuthenticated(): Boolean {
        return !getAuthToken().isNullOrBlank()
    }
}
