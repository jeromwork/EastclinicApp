package com.eastclinic.auth.presentation.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eastclinic.auth.domain.model.UserStatus
import com.eastclinic.auth.domain.usecase.GetVerificationSessionUseCase
import com.eastclinic.auth.domain.usecase.PollVerificationStatusUseCase
import com.eastclinic.core.common.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationQrViewModel @Inject constructor(
    private val getVerificationSessionUseCase: GetVerificationSessionUseCase,
    private val pollVerificationStatusUseCase: PollVerificationStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerificationQrUiState())
    val uiState: StateFlow<VerificationQrUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<VerificationQrUiEffect>()
    val uiEffect: SharedFlow<VerificationQrUiEffect> = _uiEffect.asSharedFlow()

    private var pollingJob: Job? = null

    init {
        loadSession()
    }

    fun handleEvent(event: VerificationQrUiEvent) {
        when (event) {
            VerificationQrUiEvent.LoadSession -> loadSession()
            VerificationQrUiEvent.RefreshClicked -> loadSession()
        }
    }

    private fun loadSession() {
        pollingJob?.cancel()
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = getVerificationSessionUseCase()) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, session = result.data) }
                    startPolling(result.data.sessionId)
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.error.message) }
                }
            }
        }
    }

    private fun startPolling(sessionId: String) {
        pollingJob = pollVerificationStatusUseCase(sessionId)
            .onEach { result ->
                when (result) {
                    is Result.Success -> {
                        if (result.data == UserStatus.CONFIRMED) {
                            _uiState.update { it.copy(isConfirmed = true) }
                            _uiEffect.emit(VerificationQrUiEffect.NavigateToHome("home"))
                        }
                    }
                    is Result.Error -> {
                        // Silently handle polling errors, or show a subtle indicator
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
