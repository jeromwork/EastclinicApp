package com.eastclinic.auth.presentation.login

/**
 * UI state for Login screen.
 */
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)


