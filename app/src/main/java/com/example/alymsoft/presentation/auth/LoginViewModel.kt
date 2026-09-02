package com.example.alymsoft.presentation.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.alymsoft.core.session.SessionManager
import com.example.alymsoft.data.repository.AuthRepositoryImpl
import com.example.alymsoft.domain.model.User
import com.example.alymsoft.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val identificador: String = "alymsoft@gmail.com",
    val password: String = "",
    val rememberCredentials: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
    val currentUser: User? = null
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository: AuthRepository = AuthRepositoryImpl(application)
    private val sessionManager = SessionManager.getInstance(application)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        checkExistingSession()
    }

    fun checkExistingSession() {
        if (sessionManager.isAuthenticated()) {
            val user = sessionManager.getUser()
            if (user != null) {
                _uiState.value = _uiState.value.copy(
                    isAuthenticated = true,
                    currentUser = user
                )
            } else {
                viewModelScope.launch {
                    val result = authRepository.checkSession()
                    result.onSuccess { u ->
                        _uiState.value = _uiState.value.copy(
                            isAuthenticated = true,
                            currentUser = u
                        )
                    }.onFailure {
                        sessionManager.clearSession()
                    }
                }
            }
        }
    }

    fun onIdentificadorChanged(value: String) {
        _uiState.value = _uiState.value.copy(identificador = value, errorMessage = null)
    }

    fun onPasswordChanged(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errorMessage = null)
    }

    fun onRememberCredentialsChanged(value: Boolean) {
        _uiState.value = _uiState.value.copy(rememberCredentials = value)
    }

    fun login() {
        val state = _uiState.value
        if (state.identificador.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Por favor ingresa tu correo o usuario")
            return
        }
        if (state.password.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Por favor ingresa tu contraseña")
            return
        }

        _uiState.value = state.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = authRepository.login(state.identificador, state.password)
            result.onSuccess { user ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    currentUser = user
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.localizedMessage ?: "Error al conectar con el servidor"
                )
            }
        }
    }

    fun loginDemoMode() {
        val demoUser = User(
            id = 1,
            name = "Demo Usuario",
            email = _uiState.value.identificador,
            companyName = "AlymSoft Demo"
        )
        sessionManager.saveAuthToken("demo_token_12345")
        sessionManager.saveUser(demoUser)

        _uiState.value = _uiState.value.copy(
            isLoading = false,
            isAuthenticated = true,
            currentUser = demoUser,
            errorMessage = null
        )
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = LoginUiState()
        }
    }
}
