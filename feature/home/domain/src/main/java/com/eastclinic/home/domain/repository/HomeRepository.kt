package com.eastclinic.home.domain.repository

import com.eastclinic.core.common.Result

interface HomeRepository {
    suspend fun getGreeting(): Result<String>
}




