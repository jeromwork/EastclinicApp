package com.eastclinic.home.data.repository

import com.eastclinic.core.common.AppError
import com.eastclinic.core.common.Result
import com.eastclinic.home.domain.repository.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor() : HomeRepository {
    override suspend fun getGreeting(): Result<String> {
        // Stubbed response for Step 1 skeleton
        return Result.Success("Добро пожаловать в Eastclinic!")
    }
}




