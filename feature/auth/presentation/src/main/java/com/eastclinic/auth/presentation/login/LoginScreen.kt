package com.eastclinic.auth.presentation.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToHome: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is LoginUiEffect.NavigateToHome -> {
                    onNavigateToHome("home")
                }
                is LoginUiEffect.NavigateToProfileCompletion -> {
                    onNavigateToHome("auth/profile-completion/${effect.provider}/${effect.token}")
                }
                is LoginUiEffect.LaunchSocialLogin -> {
                    // In a real app, this would launch the Google SDK
                    // For now, we simulate success with a token
                    viewModel.handleEvent(LoginUiEvent.SocialTokenReceived("google", "dummy_token"))
                }
                is LoginUiEffect.ShowError -> {
                    // Error is shown in UI state
                }
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Eastclinic Auth",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { viewModel.handleEvent(LoginUiEvent.SocialLoginClicked) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !uiState.isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Войти через Google")
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        Text("или", style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.username,
            onValueChange = { viewModel.handleEvent(LoginUiEvent.UsernameChanged(it)) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = uiState.password,
            onValueChange = { viewModel.handleEvent(LoginUiEvent.PasswordChanged(it)) },
            label = { Text("Пароль") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { viewModel.handleEvent(LoginUiEvent.LoginClicked) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Войти")
            }
        }

        // T032 [DEBUG] Simulate New User
        TextButton(
            onClick = { 
                viewModel.handleEvent(LoginUiEvent.SocialTokenReceived("google", "new_user_token"))
            }
        ) {
            Text("[DEBUG] Simulate New User (404 Handshake)")
        }
        
        uiState.error?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}



