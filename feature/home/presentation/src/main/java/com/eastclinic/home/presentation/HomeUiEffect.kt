package com.eastclinic.home.presentation

import com.eastclinic.core.ui.UiEffect

sealed interface HomeUiEffect : UiEffect {
    object NavigateToSettings : HomeUiEffect
    data class ShowMessage(val message: String) : HomeUiEffect
}




