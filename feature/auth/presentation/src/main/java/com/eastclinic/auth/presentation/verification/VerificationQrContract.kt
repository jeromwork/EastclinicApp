package com.eastclinic.auth.presentation.verification

import com.eastclinic.auth.domain.model.VerificationSession

data class VerificationQrUiState(
    val session: VerificationSession? = null,
    val isLoading: Boolean = false,
    val isConfirmed: Boolean = false,
    val error: String? = null
)

sealed class VerificationQrUiEvent {
    object LoadSession : VerificationQrUiEvent()
    object RefreshClicked : VerificationQrUiEvent()
}

sealed class VerificationQrUiEffect {
    data class NavigateToHome(val route: String) : VerificationQrUiEffect()
    data class ShowError(val message: String) : VerificationQrUiEffect()
}
