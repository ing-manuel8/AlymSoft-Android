package com.example.alymsoft.data.repository

import android.content.Context
import com.example.alymsoft.core.network.NetworkClient
import com.example.alymsoft.core.session.SessionManager
import com.example.alymsoft.data.dto.LoginRequestDTO
import com.example.alymsoft.domain.model.User
import com.example.alymsoft.domain.repository.AuthRepository

class AuthRepositoryImpl(context: Context) : AuthRepository {

    private val apiService = NetworkClient.getApiService(context)
    private val sessionManager = SessionManager.getInstance(context)

    override suspend fun login(identificador: String, password: String): Result<User> {
        return try {
            val response = apiService.login(LoginRequestDTO(identificador, password))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    var token = body.token
                    if (token.isBlank()) {
                        val cookieHeaders = response.headers().values("Set-Cookie")
                        for (cookie in cookieHeaders) {
                            if (cookie.startsWith("AuthToken=")) {
                                token = cookie.substringAfter("AuthToken=").substringBefore(";")
                                break
                            }
                        }
                    }
                    if (token.isNotBlank()) {
                        sessionManager.saveAuthToken(token)
                    }
                    val user = body.user.toDomainUser()
                    sessionManager.saveUser(user)
                    Result.success(user)
                } else {
                    Result.failure(Exception("Respuesta vacía del servidor"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "Error al iniciar sesión (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkSession(): Result<User> {
        return try {
            val response = apiService.checkSession()
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!.user.toDomainUser()
                sessionManager.saveUser(user)
                Result.success(user)
            } else {
                sessionManager.clearSession()
                Result.failure(Exception("Sesión expirada o no autorizada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            apiService.logout()
            sessionManager.clearSession()
            Result.success(Unit)
            } catch (e: Exception) {
            sessionManager.clearSession()
            Result.success(Unit)
        }
    }
}
