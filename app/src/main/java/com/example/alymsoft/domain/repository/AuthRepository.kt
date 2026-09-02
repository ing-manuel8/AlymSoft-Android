package com.example.alymsoft.domain.repository

import com.example.alymsoft.domain.model.User

interface AuthRepository {
    suspend fun login(identificador: String, password: String): Result<User>
    suspend fun checkSession(): Result<User>
    suspend fun logout(): Result<Unit>
}
