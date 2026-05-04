package com.eastclinic.auth.presentation.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eastclinic.auth.domain.model.UserProfile
import com.eastclinic.auth.domain.usecase.SocialBootstrapUseCase
import com.eastclinic.core.common.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileCompletionViewModel @Inject constructor(
    private val bootstrapSocialUseCase: SocialBootstrapUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val provider: String = savedStateHandle.get<String>("provider") ?: ""
    private val token: String = savedStateHandle.get<String>("token") ?: ""

    private val _uiState = MutableStateFlow(ProfileCompletionUiState(provider = provider, token = token))
    val uiState: StateFlow<ProfileCompletionUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<ProfileCompletionUiEffect>()
    val uiEffect: SharedFlow<ProfileCompletionUiEffect> = _uiEffect.asSharedFlow()

    fun handleEvent(event: ProfileCompletionUiEvent) {
        when (event) {
            is ProfileCompletionUiEvent.FirstNameChanged -> _uiState.update { it.copy(firstName = event.value) }
            is ProfileCompletionUiEvent.LastNameChanged -> _uiState.update { it.copy(lastName = event.value) }
            is ProfileCompletionUiEvent.MiddleNameChanged -> _uiState.update { it.copy(middleName = event.value) }
            is ProfileCompletionUiEvent.PhoneChanged -> _uiState.update { it.copy(phone = event.value) }
            ProfileCompletionUiEvent.SubmitClicked -> submit()
        }
    }

    private fun submit() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.firstName.isBlank() || state.lastName.isBlank() || state.phone.isBlank()) {
                _uiEffect.emit(ProfileCompletionUiEffect.ShowError("Пожалуйста, заполните обязательные поля"))
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, error = null) }

            val profile = UserProfile(
                firstName = state.firstName,
                lastName = state.lastName,
                middleName = state.middleName.ifBlank { null },
                phone = state.phone
            )

            val result = bootstrapSocialUseCase(state.provider, state.token, profile)

            _uiState.update { it.copy(isLoading = false) }

            when (result) {
                is Result.Success -> {
                    _uiEffect.emit(ProfileCompletionUiEffect.NavigateToHome("home"))
                }
                is Result.Error -> {
                    _uiEffect.emit(ProfileCompletionUiEffect.ShowError(result.error.message))
                }
            }
        }
    }
}
