package com.eastclinic.home.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.eastclinic.home.presentation.HomeScreen
import com.eastclinic.home.presentation.SettingsScreen
import com.eastclinic.home.presentation.SplashScreen

fun NavGraphBuilder.homeGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = HomeRoutes.SPLASH,
        route = HomeRoutes.ROOT
    ) {
        composable(HomeRoutes.SPLASH) {
            SplashScreen(
                onFinished = {
                    navController.navigate(HomeRoutes.HOME) {
                        popUpTo(HomeRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(HomeRoutes.HOME) {
            HomeScreen(
                onNavigateToSettings = {
                    navController.navigate(HomeRoutes.SETTINGS)
                }
            )
        }

        composable(HomeRoutes.SETTINGS) {
            SettingsScreen()
        }
    }
}




