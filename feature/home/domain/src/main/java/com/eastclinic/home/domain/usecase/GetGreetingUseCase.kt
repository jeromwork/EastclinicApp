package com.eastclinic.home.domain.usecase

import com.eastclinic.core.common.Result
import com.eastclinic.home.domain.repository.HomeRepository
import javax.inject.Inject

class GetGreetingUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(): Result<String> = repository.getGreeting()
}




