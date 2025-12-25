package com.eastclinic.home.presentation

sealed class HomeUiEvent {
    object Refresh : HomeUiEvent()
    object NavigateToSettings : HomeUiEvent()
}




