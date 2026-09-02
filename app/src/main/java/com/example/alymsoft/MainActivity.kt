package com.example.alymsoft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alymsoft.presentation.auth.LoginScreen
import com.example.alymsoft.presentation.auth.LoginViewModel
import com.example.alymsoft.presentation.main.MainScreen
import com.example.alymsoft.ui.theme.AlymSoftTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlymSoftTheme {
                val loginViewModel: LoginViewModel = viewModel()
                val uiState by loginViewModel.uiState.collectAsState()

                Surface(modifier = Modifier.fillMaxSize()) {
                    Crossfade(targetState = uiState.isAuthenticated, label = "AuthCrossfade") { isAuthenticated ->
                        if (isAuthenticated) {
                            MainScreen(
                                user = uiState.currentUser,
                                onLogoutClick = { loginViewModel.logout() }
                            )
                        } else {
                            LoginScreen(viewModel = loginViewModel)
                        }
                    }
                }
            }
        }
    }
}