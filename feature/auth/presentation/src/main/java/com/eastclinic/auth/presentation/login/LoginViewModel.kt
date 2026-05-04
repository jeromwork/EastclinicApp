package com.eastclinic.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eastclinic.auth.domain.repository.AuthRepository
import com.eastclinic.core.common.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    private val _uiEffect = kotlinx.coroutines.flow.MutableSharedFlow<LoginUiEffect>()
    val uiEffect: SharedFlow<LoginUiEffect> = _uiEffect.asSharedFlow()
    
    fun handleEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.UsernameChanged -> {
                _uiState.update { it.copy(username = event.username) }
            }
            is LoginUiEvent.PasswordChanged -> {
                _uiState.update { it.copy(password = event.password) }
            }
            is LoginUiEvent.LoginClicked -> {
                login()
            }
            is LoginUiEvent.SocialLoginClicked -> {
                viewModelScope.launch {
                    _uiEffect.emit(LoginUiEffect.LaunchSocialLogin)
                }
            }
            is LoginUiEvent.SocialTokenReceived -> {
                socialLogin(event.provider, event.token)
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.login(_uiState.value.username, _uiState.value.password)
            _uiState.update { it.copy(isLoading = false) }
            when (result) {
                is Result.Success -> _uiEffect.emit(LoginUiEffect.NavigateToHome("home"))
                is Result.Error -> _uiEffect.emit(LoginUiEffect.ShowError(result.error.message))
            }
        }
    }
    
    private fun socialLogin(provider: String, token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = authRepository.handshakeSocial(provider, token)
            
            _uiState.update { it.copy(isLoading = false) }
            
            when (result) {
                is Result.Success -> {
                    // Success means session established
                    _uiEffect.emit(LoginUiEffect.NavigateToHome("home"))
                }
                is Result.Error -> {
                    // If error is 404 (or specific status), it might mean new user
                    // In our case, Backend handshake returns 401/404 if user not found
                    // Or we can check a specific error type
                    if (result.error.message.contains("404") || result.error.message.contains("not found")) {
                        _uiEffect.emit(LoginUiEffect.NavigateToProfileCompletion(provider, token))
                    } else {
                        _uiEffect.emit(LoginUiEffect.ShowError(result.error.message))
                    }
                }
            }
        }
    }
}



