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

    private val _uiEffect = MutableSharedFlow<HomeUiEffect>()
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
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = getGreeting()) {
                is Result.Success -> _uiState.update {
                    it.copy(isLoading = false, greeting = result.data)
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.error.message)
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




