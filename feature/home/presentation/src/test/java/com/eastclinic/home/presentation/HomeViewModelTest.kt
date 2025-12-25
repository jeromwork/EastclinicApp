package com.eastclinic.home.presentation

import app.cash.turbine.test
import com.eastclinic.core.async.TestDispatchers
import com.eastclinic.core.common.Result
import com.eastclinic.home.domain.repository.HomeRepository
import com.eastclinic.home.domain.usecase.GetGreetingUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private class FakeHomeRepository(
    private val result: Result<String> = Result.Success("Добро пожаловать в Eastclinic!")
) : HomeRepository {
    override suspend fun getGreeting(): Result<String> = result
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @Test
    fun `loads greeting successfully`() = runTest {
        val repo = FakeHomeRepository()
        val vm = HomeViewModel(
            getGreeting = GetGreetingUseCase(repo),
            dispatchers = TestDispatchers(testDispatcher = StandardTestDispatcher(this.testScheduler))
        )

        assertEquals(false, vm.uiState.value.isLoading)
        assertEquals("Добро пожаловать в Eastclinic!", vm.uiState.value.greeting)
    }

    @Test
    fun `emits navigation effect to settings`() = runTest {
        val repo = FakeHomeRepository()
        val vm = HomeViewModel(
            getGreeting = GetGreetingUseCase(repo),
            dispatchers = TestDispatchers(testDispatcher = StandardTestDispatcher(this.testScheduler))
        )

        vm.uiEffect.test {
            vm.onEvent(HomeUiEvent.NavigateToSettings)
            assertEquals(HomeUiEffect.NavigateToSettings, awaitItem())
        }
    }
}


