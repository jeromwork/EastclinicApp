package com.eastclinic.auth.presentation.login

/**
 * UI events for Login screen.
 */
sealed class LoginUiEvent {
    data class UsernameChanged(val username: String) : LoginUiEvent()
    data class PasswordChanged(val password: String) : LoginUiEvent()
    object LoginClicked : LoginUiEvent()
}


