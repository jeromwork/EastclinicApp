package com.eastclinic.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.eastclinic.home.presentation.navigation.HomeRoutes
import com.eastclinic.home.presentation.navigation.homeGraph

@Composable
fun RootNavGraph(
    navController: NavHostController,
    startDestination: String = HomeRoutes.SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        homeGraph(navController)
    }
}


