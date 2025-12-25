package com.eastclinic.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eastclinic.core.async.DispatcherProvider
import com.eastclinic.core.common.Result
import com.eastclinic.home.domain.usecase.GetGreetingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getGreeting: GetGreetingUseCase,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // SharedFlow с буфером для предотвращения потери событий
    // replay=0 - не повторяем старые события новым подписчикам
    // extraBufferCapacity=64 - буфер для событий, если подписчик не готов
    private val _uiEffect = MutableSharedFlow<HomeUiEffect>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.SUSPEND
    )
    val uiEffect: SharedFlow<HomeUiEffect> = _uiEffect.asSharedFlow()

    init {
        loadGreeting()
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.Refresh -> loadGreeting()
            HomeUiEvent.NavigateToSettings -> emitEffect(HomeUiEffect.NavigateToSettings)
        }
    }

    private fun loadGreeting() {
        viewModelScope.launch(dispatchers.io) {
            // Обновление loading state на Main для UI-потока
            launch(dispatchers.main) {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }
            
            when (val result = getGreeting()) {
                is Result.Success -> {
                    // Мутации UI state всегда на Main
                    launch(dispatchers.main) {
                        _uiState.update {
                            it.copy(isLoading = false, greeting = result.data)
                        }
                    }
                }
                is Result.Error -> {
                    // Мутации UI state всегда на Main
                    launch(dispatchers.main) {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = result.error.userMessage)
                        }
                    }
                    // Эффект для показа snackbar (если нужен)
                    launch(dispatchers.main) {
                        _uiEffect.emit(HomeUiEffect.ShowMessage(result.error.userMessage))
                    }
                }
            }
        }
    }

    private fun emitEffect(effect: HomeUiEffect) {
        viewModelScope.launch(dispatchers.main) {
            _uiEffect.emit(effect)
        }
    }
}




