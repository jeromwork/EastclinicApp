package com.eastclinic.auth.presentation.login

/**
 * UI events for Login screen.
 */
sealed class LoginUiEvent {
    data class UsernameChanged(val username: String) : LoginUiEvent()
    data class PasswordChanged(val password: String) : LoginUiEvent()
    object LoginClicked : LoginUiEvent()
    object SocialLoginClicked : LoginUiEvent()
    data class SocialTokenReceived(val provider: String, val token: String) : LoginUiEvent()
}



