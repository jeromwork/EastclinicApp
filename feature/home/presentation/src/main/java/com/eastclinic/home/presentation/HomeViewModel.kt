package com.eastclinic.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eastclinic.core.auth.SessionStore
import com.eastclinic.core.auth.SessionUserStatus
import com.eastclinic.core.common.Result
import com.eastclinic.core.common.getOrNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val userStatus: SessionUserStatus = SessionUserStatus.CONFIRMED,
    val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sessionStore: SessionStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadStatus()
    }

    fun loadStatus() {
        viewModelScope.launch {
            val session = sessionStore.getSession().getOrNull()
            _uiState.update { it.copy(userStatus = session?.status ?: SessionUserStatus.CONFIRMED) }
        }
    }
}
