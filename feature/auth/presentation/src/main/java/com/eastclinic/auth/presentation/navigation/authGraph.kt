package com.eastclinic.auth.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.eastclinic.auth.presentation.login.LoginScreen

fun NavGraphBuilder.authGraph(
    onNavigateToHome: (String) -> Unit
) {
    composable(AuthRoutes.LOGIN) {
        LoginScreen(onNavigateToHome = onNavigateToHome)
    }
}
