package com.eastclinic.auth.presentation.login

import com.eastclinic.core.ui.UiEffect

/**
 * UI effects for Login screen (extends base UiEffect).
 */
sealed class LoginUiEffect : UiEffect {
    data class NavigateToHome(val route: String) : LoginUiEffect()
    data class ShowError(val message: String) : LoginUiEffect()
}
