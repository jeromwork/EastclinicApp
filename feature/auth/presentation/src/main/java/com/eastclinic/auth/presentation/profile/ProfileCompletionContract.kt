package com.eastclinic.auth.presentation.profile

data class ProfileCompletionUiState(
    val provider: String = "",
    val token: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val middleName: String = "",
    val phone: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class ProfileCompletionUiEvent {
    data class FirstNameChanged(val value: String) : ProfileCompletionUiEvent()
    data class LastNameChanged(val value: String) : ProfileCompletionUiEvent()
    data class MiddleNameChanged(val value: String) : ProfileCompletionUiEvent()
    data class PhoneChanged(val value: String) : ProfileCompletionUiEvent()
    object SubmitClicked : ProfileCompletionUiEvent()
}

sealed class ProfileCompletionUiEffect {
    data class NavigateToHome(val route: String) : ProfileCompletionUiEffect()
    data class ShowError(val message: String) : ProfileCompletionUiEffect()
}
