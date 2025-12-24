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
        }
    }
    
    private fun login() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = authRepository.login(
                username = _uiState.value.username,
                password = _uiState.value.password
            )
            
            _uiState.update { it.copy(isLoading = false) }
            
            when (result) {
                is Result.Success -> {
                    _uiEffect.emit(LoginUiEffect.NavigateToHome("home"))
                }
                is Result.Error -> {
                    _uiEffect.emit(LoginUiEffect.ShowError(result.error.message))
                }
            }
        }
    }
}


