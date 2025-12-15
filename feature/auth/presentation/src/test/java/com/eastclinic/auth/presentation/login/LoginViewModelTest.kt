package com.eastclinic.auth.presentation.login

import app.cash.turbine.test
import com.eastclinic.auth.domain.model.User
import com.eastclinic.auth.domain.repository.AuthRepository
import com.eastclinic.core.common.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class LoginViewModelTest {
    
    private val authRepository = mockk<AuthRepository>()
    private val viewModel = LoginViewModel(authRepository)
    
    @Test
    fun `initial state is correct`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("", state.username)
            assertEquals("", state.password)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }
    }
    
    @Test
    fun `username changed updates state`() = runTest {
        viewModel.handleEvent(LoginUiEvent.UsernameChanged("testuser"))
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("testuser", state.username)
        }
    }
    
    @Test
    fun `password changed updates state`() = runTest {
        viewModel.handleEvent(LoginUiEvent.PasswordChanged("password123"))
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("password123", state.password)
        }
    }
    
    @Test
    fun `login success emits navigate effect`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns Result.Success(User("1", "testuser"))
        
        viewModel.handleEvent(LoginUiEvent.UsernameChanged("testuser"))
        viewModel.handleEvent(LoginUiEvent.PasswordChanged("password"))
        viewModel.handleEvent(LoginUiEvent.LoginClicked)
        
        viewModel.uiEffect.test {
            val effect = awaitItem()
            assertTrue(effect is LoginUiEffect.NavigateToHome)
        }
    }
    
    @Test
    fun `login error emits show error effect`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns Result.Error(
            com.eastclinic.core.common.AppError.NetworkError("Login failed", 401)
        )
        
        viewModel.handleEvent(LoginUiEvent.UsernameChanged("testuser"))
        viewModel.handleEvent(LoginUiEvent.PasswordChanged("password"))
        viewModel.handleEvent(LoginUiEvent.LoginClicked)
        
        viewModel.uiEffect.test {
            val effect = awaitItem()
            assertTrue(effect is LoginUiEffect.ShowError)
            assertEquals("Login failed", (effect as LoginUiEffect.ShowError).message)
        }
    }
}
