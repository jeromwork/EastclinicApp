package com.eastclinic.auth.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.eastclinic.auth.presentation.login.LoginScreen
import com.eastclinic.auth.presentation.profile.ProfileCompletionScreen
import com.eastclinic.auth.presentation.verification.VerificationQrScreen

fun NavGraphBuilder.authGraph(
    onNavigateToHome: (String) -> Unit
) {
    composable(AuthRoutes.LOGIN) {
        LoginScreen(onNavigateToHome = onNavigateToHome)
    }

    composable(
        route = AuthRoutes.PROFILE_COMPLETION,
        arguments = listOf(
            navArgument("provider") { type = NavType.StringType },
            navArgument("token") { type = NavType.StringType }
        )
    ) {
        ProfileCompletionScreen(onNavigateToHome = onNavigateToHome)
    }

    composable(AuthRoutes.VERIFICATION) {
        VerificationQrScreen(onNavigateToHome = onNavigateToHome)
    }
}



