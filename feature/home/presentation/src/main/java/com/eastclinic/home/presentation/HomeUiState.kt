package com.eastclinic.home.presentation

data class HomeUiState(
    val isLoading: Boolean = true,
    val greeting: String = "",
    val errorMessage: String? = null
)




